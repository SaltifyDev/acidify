package org.ntqqrev.yogurt.api.message

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.message.MessageScene
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetMessageOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.transform.toMessageScene
import org.ntqqrev.yogurt.transform.transformMessage
import org.ntqqrev.yogurt.util.invoke

val GetMessage = ApiEndpoint.GetMessage {
    val bot = application.dependencies.resolve<Bot>()

    val messages = when (it.messageScene.toMessageScene()) {
        MessageScene.FRIEND -> {
            bot.getFriend(it.peerId)
                ?: throw MilkyApiException(-404, "Friend not found")

            bot.getFriendHistoryMessages(
                friendUin = it.peerId,
                limit = 1,
                startSequence = it.messageSeq
            )
        }

        MessageScene.GROUP -> {
            bot.getGroup(it.peerId)
                ?: throw MilkyApiException(-404, "Group not found")

            bot.getGroupHistoryMessages(
                groupUin = it.peerId,
                limit = 1,
                startSequence = it.messageSeq
            )
        }

        else -> throw MilkyApiException(-400, "Unsupported message scene")
    }

    val message = messages.messages.firstOrNull()
        ?: throw MilkyApiException(-404, "Message not found")

    val transformedMessage = application.transformMessage(message)
        ?: throw MilkyApiException(-404, "Message transformation failed")

    GetMessageOutput(
        message = transformedMessage
    )
}

