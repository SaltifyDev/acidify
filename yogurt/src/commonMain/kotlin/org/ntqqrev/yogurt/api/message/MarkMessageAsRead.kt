package org.ntqqrev.yogurt.api.message

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.message.MessageScene
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.MarkMessageAsReadOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.transform.toMessageScene
import org.ntqqrev.yogurt.util.invoke

val MarkMessageAsRead = ApiEndpoint.MarkMessageAsRead {
    val bot = application.dependencies.resolve<Bot>()

    when (it.messageScene.toMessageScene()) {
        MessageScene.FRIEND -> {
            bot.getFriend(it.peerId) ?: throw MilkyApiException(-404, "Friend not found")

            // Get the message time from history
            val messages = bot.getFriendHistoryMessages(
                friendUin = it.peerId,
                limit = 1,
                startSequence = it.messageSeq
            )
            val message = messages.messages.firstOrNull()
                ?: throw MilkyApiException(-404, "Message not found")

            bot.markFriendMessagesAsRead(
                friendUin = it.peerId,
                startSequence = it.messageSeq,
                startTime = message.timestamp
            )
        }

        MessageScene.GROUP -> {
            bot.getGroup(it.peerId) ?: throw MilkyApiException(-404, "Group not found")

            bot.markGroupMessagesAsRead(
                groupUin = it.peerId,
                startSequence = it.messageSeq
            )
        }

        else -> throw MilkyApiException(-400, "Unsupported message scene")
    }

    MarkMessageAsReadOutput()
}

