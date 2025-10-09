package org.ntqqrev.yogurt.event

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import io.ktor.server.routing.application
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.util.log.Logger
import org.ntqqrev.yogurt.transform.transformAcidifyEvent

fun Route.configureMilkyEventWebSocket() {
    webSocket {
        val bot = application.dependencies.resolve<Bot>()
        val logger = application.dependencies.resolve<Logger>()
        logger.i { "${call.request.local.remoteAddress} 通过 WebSocket 连接" }
        launch {
            bot.eventFlow.collect { event ->
                application.transformAcidifyEvent(event)?.let { sendSerialized(it) }
            }
        }
        val reason = closeReason.await()
        if (reason?.code == CloseReason.Codes.NORMAL.code) {
            logger.i { "${call.request.local.remoteAddress} 断开 WebSocket 连接" }
        } else {
            logger.w { "${call.request.local.remoteAddress} 异常断开 WebSocket 连接 $reason" }
        }
    }
}