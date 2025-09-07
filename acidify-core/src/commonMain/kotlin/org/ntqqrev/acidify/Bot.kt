package org.ntqqrev.acidify

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.ntqqrev.acidify.common.AppInfo
import org.ntqqrev.acidify.common.QrCodeState
import org.ntqqrev.acidify.common.SessionStore
import org.ntqqrev.acidify.common.SignProvider
import org.ntqqrev.acidify.event.AcidifyEvent
import org.ntqqrev.acidify.event.QrCodeGeneratedEvent
import org.ntqqrev.acidify.event.QrCodeStateQueryEvent
import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.service.system.FetchQrCode
import org.ntqqrev.acidify.internal.service.system.QueryQrCodeState
import org.ntqqrev.acidify.internal.service.system.WtLogin

/**
 * Acidify Bot 实例
 */
class Bot internal constructor(internal val client: LagrangeClient) {
    internal val sharedEventFlow = MutableSharedFlow<AcidifyEvent>(
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * [AcidifyEvent] 流，可用于监听各种事件
     *
     * 示例：
     * ```
     * bot.eventFlow.collect { event ->
     *     when (event) {
     *         is QrCodeGeneratedEvent -> {
     *             println("QR Code URL: ${event.url}")
     *         }
     *     }
     * }
     * ```
     *
     * 注意 `collect` 是一个 `suspend` 函数，强烈建议在与 Bot 实例相同的 [CoroutineScope] 中使用。
     */
    val eventFlow: SharedFlow<AcidifyEvent>
        get() = sharedEventFlow

    /**
     * 发起二维码登录请求。过程中会触发事件：
     * - [QrCodeGeneratedEvent]：当二维码生成时触发，包含二维码链接和 PNG 图片数据
     * - [QrCodeStateQueryEvent]：每次查询二维码状态时触发，包含当前二维码状态（例如未扫码、已扫码未确认、已确认等）
     * @param queryInterval 查询间隔（单位 ms），不能小于 `1000`
     * @throws org.ntqqrev.acidify.exception.WtLoginException 当二维码扫描成功，但后续登录失败时抛出
     * @throws IllegalStateException 当二维码过期或用户取消登录时抛出
     * @see QrCodeState
     */
    suspend fun qrCodeLogin(queryInterval: Long = 3000L) {
        require(queryInterval >= 1000L) { "查询间隔不能小于 1000 毫秒" }
        val qrCode = client.callService(FetchQrCode)
        sharedEventFlow.emit(QrCodeGeneratedEvent(qrCode.qrCodeUrl, qrCode.qrCodePng))

        while (true) {
            val state = client.callService(QueryQrCodeState)
            sharedEventFlow.emit(QrCodeStateQueryEvent(state))
            when (state) {
                QrCodeState.CONFIRMED -> break
                QrCodeState.CODE_EXPIRED -> throw IllegalStateException("二维码已过期")
                QrCodeState.CANCELLED -> throw IllegalStateException("用户取消了登录")
                QrCodeState.UNKNOWN -> throw IllegalStateException("未知的二维码状态")
                else -> {} // pass
            }
            delay(queryInterval)
        }

        client.callService(WtLogin)
        // todo: implement bot online
    }

    companion object {
        /**
         * 创建新的 Bot 实例
         */
        suspend fun create(
            appInfo: AppInfo,
            sessionStore: SessionStore,
            signProvider: SignProvider,
            scope: CoroutineScope
        ): Bot {
            val client = LagrangeClient(appInfo, sessionStore, signProvider, scope)
            val bot = Bot(client)
            bot.client.packetLogic.connect()
            return bot
        }
    }
}