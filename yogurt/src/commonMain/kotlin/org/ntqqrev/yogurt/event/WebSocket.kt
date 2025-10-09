package org.ntqqrev.yogurt.event

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import io.ktor.server.routing.application
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import kotlinx.coroutines.channels.ClosedReceiveChannelException
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
        try {
            incoming.receive()
        } catch (_: ClosedReceiveChannelException) {
            logger.i { "${call.request.local.remoteAddress} 断开 WebSocket 连接" }
        }
    }
}