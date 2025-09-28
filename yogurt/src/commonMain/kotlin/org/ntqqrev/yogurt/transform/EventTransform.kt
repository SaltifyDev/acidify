package org.ntqqrev.yogurt.transform

import io.ktor.server.application.Application
import org.ntqqrev.acidify.event.AcidifyEvent
import org.ntqqrev.acidify.event.MessageReceiveEvent
import org.ntqqrev.milky.Event

suspend fun Application.transformAcidifyEvent(event: AcidifyEvent): Event? {
    return when (event) {
        is MessageReceiveEvent -> Event.MessageReceive(
            data = transformMessage(event.message) ?: return null
        )

        else -> null
    }
}