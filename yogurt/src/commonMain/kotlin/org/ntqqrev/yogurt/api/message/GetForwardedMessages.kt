package org.ntqqrev.yogurt.api.message

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetForwardedMessagesOutput
import org.ntqqrev.milky.IncomingForwardedMessage
import org.ntqqrev.yogurt.transform.transformSegment
import org.ntqqrev.yogurt.util.invoke

val GetForwardedMessages = ApiEndpoint.GetForwardedMessages {
    val bot = application.dependencies.resolve<Bot>()

    val forwardedMessages = bot.getForwardedMessages(it.forwardId)

    val transformedMessages = forwardedMessages.map { msg ->
        IncomingForwardedMessage(
            senderName = msg.senderName,
            avatarUrl = msg.avatarUrl,
            time = msg.timestamp,
            segments = msg.segments.map { segment ->
                application.transformSegment(segment)
            }
        )
    }

    GetForwardedMessagesOutput(
        messages = transformedMessages
    )
}

