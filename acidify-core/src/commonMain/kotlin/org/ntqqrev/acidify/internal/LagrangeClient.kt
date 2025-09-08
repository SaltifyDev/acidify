package org.ntqqrev.acidify.internal

import kotlinx.coroutines.CoroutineScope
import org.ntqqrev.acidify.common.AppInfo
import org.ntqqrev.acidify.common.SessionStore
import org.ntqqrev.acidify.common.SignProvider
import org.ntqqrev.acidify.exception.ServiceException
import org.ntqqrev.acidify.internal.logic.LoginLogic
import org.ntqqrev.acidify.internal.logic.PacketLogic
import org.ntqqrev.acidify.internal.service.Service

internal class LagrangeClient(
    val appInfo: AppInfo,
    val sessionStore: SessionStore,
    val signProvider: SignProvider,
    val scope: CoroutineScope
) {
    val loginLogic = LoginLogic(this)
    val packetLogic = PacketLogic(this)

    suspend fun <T, R> callService(service: Service<T, R>, payload: T): R {
        val byteArray = service.build(this, payload)
        val resp = packetLogic.sendPacket(service.cmd, byteArray)
        if (resp.retCode != 0) {
            throw ServiceException(
                service.cmd,
                resp.retCode,
                resp.extra ?: ""
            )
        }
        return service.parse(this, resp.response)
    }

    suspend fun <R> callService(service: Service<Unit, R>): R {
        return callService(service, Unit)
    }
}