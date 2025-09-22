package org.ntqqrev.acidify

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.ntqqrev.acidify.common.AppInfo
import org.ntqqrev.acidify.common.QRCodeState
import org.ntqqrev.acidify.common.SessionStore
import org.ntqqrev.acidify.common.SignProvider
import org.ntqqrev.acidify.common.log.LogHandler
import org.ntqqrev.acidify.common.log.LogLevel
import org.ntqqrev.acidify.common.log.LogMessage
import org.ntqqrev.acidify.common.log.Logger
import org.ntqqrev.acidify.common.struct.BotFriendData
import org.ntqqrev.acidify.common.struct.BotGroupData
import org.ntqqrev.acidify.event.AcidifyEvent
import org.ntqqrev.acidify.event.QRCodeGeneratedEvent
import org.ntqqrev.acidify.event.QRCodeStateQueryEvent
import org.ntqqrev.acidify.event.SessionStoreUpdatedEvent
import org.ntqqrev.acidify.exception.BotOnlineException
import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.service.common.FetchFriends
import org.ntqqrev.acidify.internal.service.common.FetchGroups
import org.ntqqrev.acidify.internal.service.system.*

/**
 * Acidify Bot 实例
 */
class Bot internal constructor(
    val appInfo: AppInfo,
    val sessionStore: SessionStore,
    val signProvider: SignProvider,
    val scope: CoroutineScope
) {
    private val logger = this.createLogger(this)
    internal val client = LagrangeClient(
        appInfo, sessionStore, signProvider, scope,
        this::createLogger
    )
    internal val sharedEventFlow = MutableSharedFlow<AcidifyEvent>(
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    internal val sharedLogFlow = MutableSharedFlow<LogMessage>(
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * [AcidifyEvent] 流，可用于监听各种事件
     *
     * 示例：
     * ```
     * bot.eventFlow.collect { event ->
     *     when (event) {
     *         is QRCodeGeneratedEvent -> {
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
     * 当前登录用户的 uin（QQ 号）
     */
    val uin: Long
        get() = sessionStore.uin.takeIf { it != 0L }
            ?: throw IllegalStateException("用户尚未登录")

    /**
     * 当前登录用户的 uid
     */
    val uid: String
        get() = sessionStore.uid.takeIf { it.isNotEmpty() }
            ?: throw IllegalStateException("用户尚未登录")

    /**
     * 表示当前 Bot 是否已登录
     */
    var isLoggedIn: Boolean = false
        internal set

    /**
     * 创建一个 [Logger] 实例，通常用于库内部日志记录，并将产生的日志发送到提供的 [LogHandler]。
     */
    fun createLogger(fromObject: Any): Logger {
        return Logger(
            this,
            fromObject::class.qualifiedName
                ?: throw IllegalStateException("Cannot create logger for anonymous class")
        )
    }

    /**
     * 发起二维码登录请求。过程中会触发事件：
     * - [QRCodeGeneratedEvent]：当二维码生成时触发，包含二维码链接和 PNG 图片数据
     * - [QRCodeStateQueryEvent]：每次查询二维码状态时触发，包含当前二维码状态（例如未扫码、已扫码未确认、已确认等）
     * @param queryInterval 查询间隔（单位 ms），不能小于 `1000`
     * @throws org.ntqqrev.acidify.exception.WtLoginException 当二维码扫描成功，但后续登录失败时抛出
     * @throws IllegalStateException 当二维码过期或用户取消登录时抛出
     * @see QRCodeState
     */
    suspend fun qrCodeLogin(queryInterval: Long = 3000L) {
        require(queryInterval >= 1000L) { "查询间隔不能小于 1000 毫秒" }
        val qrCode = client.callService(FetchQRCode)
        logger.i { "二维码 URL：${qrCode.qrCodeUrl}" }
        sharedEventFlow.emit(QRCodeGeneratedEvent(qrCode.qrCodeUrl, qrCode.qrCodePng))

        while (true) {
            val state = client.callService(QueryQRCodeState)
            logger.d { "二维码状态：${state.name} (${state.value})" }
            sharedEventFlow.emit(QRCodeStateQueryEvent(state))
            when (state) {
                QRCodeState.CONFIRMED -> break
                QRCodeState.CODE_EXPIRED -> throw IllegalStateException("二维码已过期")
                QRCodeState.CANCELLED -> throw IllegalStateException("用户取消了登录")
                QRCodeState.UNKNOWN -> throw IllegalStateException("未知的二维码状态")
                else -> {} // pass
            }
            delay(queryInterval)
        }

        client.callService(WtLogin)
        logger.d { "成功获取 $uin 的登录凭据" }
        sharedEventFlow.emit(SessionStoreUpdatedEvent(sessionStore))
        online()
    }

    /**
     * 尝试使用现有的 Session 信息上线。
     * 请优先调用 [tryLogin]，该方法会在现有 Session 失效时自动调用 [qrCodeLogin]。
     * 若确定 Session 有效且不希望进行二维码登录，可调用此方法。
     */
    suspend fun online() {
        val result = client.callService(BotOnline)
        if (result != "register success") {
            throw BotOnlineException(result)
        }
        isLoggedIn = true
        logger.i { "用户 $uin 已上线" }
        // todo: post online logic
        // - heartbeat
        // - fetch friends/groups
        // - get face details
        // - get highway url
    }

    /**
     * 下线 Bot，释放资源。
     */
    suspend fun offline() {
        client.callService(BotOffline)
        logger.i { "用户 $uin 已下线" }
    }

    /**
     * 先尝试使用现有的 Session 信息登录，若失败则调用 [qrCodeLogin] 重新登录。
     * 如果是第一次登录，请务必调用 [qrCodeLogin]。
     */
    suspend fun tryLogin() {
        try {
            online()
        } catch (e: Exception) {
            logger.w(e) { "使用现有 Session 登录失败，尝试二维码登录" }
            sessionStore.clear()
            sharedEventFlow.emit(SessionStoreUpdatedEvent(sessionStore))
            qrCodeLogin()
        }
    }

    /**
     * 获取好友与好友分组信息。
     */
    suspend fun fetchFriends(): List<BotFriendData> {
        var nextUin: Long? = null
        val friendDataResult = mutableListOf<BotFriendData>()
        do {
            val resp = client.callService(FetchFriends, FetchFriends.Req(nextUin))
            nextUin = resp.nextUin
            friendDataResult.addAll(resp.friendDataList)
        } while (nextUin != null)
        return friendDataResult
    }

    /**
     * 获取群信息。
     */
    suspend fun fetchGroups(): List<BotGroupData> {
        return client.callService(FetchGroups)
    }

    companion object {
        /**
         * 创建新的 Bot 实例
         */
        suspend fun create(
            appInfo: AppInfo,
            sessionStore: SessionStore,
            signProvider: SignProvider,
            scope: CoroutineScope,
            minLogLevel: LogLevel,
            logHandler: LogHandler,
        ): Bot {
            val bot = Bot(appInfo, sessionStore, signProvider, scope)
            scope.launch {
                bot.sharedLogFlow
                    .filter { it.level >= minLogLevel }
                    .collect {
                        logHandler.handleLog(
                            it.level,
                            it.tag,
                            it.messageSupplier(),
                            it.throwable
                        )
                    }
            }
            bot.client.packetLogic.connect()
            return bot
        }
    }
}