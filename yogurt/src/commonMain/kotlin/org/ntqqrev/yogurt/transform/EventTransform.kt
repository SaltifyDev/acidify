package org.ntqqrev.yogurt.transform

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.event.AcidifyEvent
import org.ntqqrev.acidify.event.MessageReceiveEvent
import org.ntqqrev.milky.Event
import org.ntqqrev.yogurt.YogurtApp.config

suspend fun Application.transformAcidifyEvent(event: AcidifyEvent): Event? {
    val bot = dependencies.resolve<Bot>()
    return when (event) {
        is MessageReceiveEvent -> {
            if (config.reportSelfMessage || event.message.senderUin != bot.uin) {
                Event.MessageReceive(
                    data = transformMessage(event.message) ?: return null
                )
            } else {
                null
            }
        }

        else -> null
    }
}