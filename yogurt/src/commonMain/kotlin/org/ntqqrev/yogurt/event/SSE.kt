package org.ntqqrev.yogurt.event

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import kotlinx.coroutines.launch
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.milkyJsonModule
import org.ntqqrev.yogurt.transform.transformAcidifyEvent

fun Route.configureMilkyEventSse() {
    sse {
        val bot = application.dependencies.resolve<Bot>()
        val logger = bot.createLogger("SseModule")
        logger.i { "${call.request.local.remoteAddress} 通过 SSE 连接" }
        launch {
            bot.eventFlow.collect { event ->
                application.transformAcidifyEvent(event)?.let {
                    send(
                        data = milkyJsonModule.encodeToString(it),
                        event = "milky_event"
                    )
                }
            }
        }
    }
}