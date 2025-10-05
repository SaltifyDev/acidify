package org.ntqqrev.yogurt.event

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import kotlinx.coroutines.launch
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.util.log.Logger
import org.ntqqrev.milky.milkyJsonModule
import org.ntqqrev.yogurt.transform.transformAcidifyEvent
import org.ntqqrev.yogurt.util.PreprocessedEventFlow

fun Route.configureMilkyEventSse() {
    sse {
        val eventFlow = application.dependencies.resolve<PreprocessedEventFlow>()
        val logger = application.dependencies.resolve<Logger>()
        logger.i { "${call.request.local.remoteAddress} 通过 SSE 连接" }
        launch {
            eventFlow.collect { event ->
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