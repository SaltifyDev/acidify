package org.ntqqrev.yogurt.api.message

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.message.MessageScene
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetHistoryMessagesOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.transform.toMessageScene
import org.ntqqrev.yogurt.transform.transformMessage
import org.ntqqrev.yogurt.util.FriendCache
import org.ntqqrev.yogurt.util.GroupCache
import org.ntqqrev.yogurt.util.invoke

val GetHistoryMessages = ApiEndpoint.GetHistoryMessages {
    val bot = application.dependencies.resolve<Bot>()

    if (it.limit !in 1..30) {
        throw MilkyApiException(-400, "Limit must be between 1 and 30")
    }

    val historyMessages = when (it.messageScene.toMessageScene()) {
        MessageScene.FRIEND -> {
            val friendCache = application.dependencies.resolve<FriendCache>()
            friendCache[it.peerId, true]
                ?: throw MilkyApiException(-404, "Friend not found")

            bot.getFriendHistoryMessages(
                friendUin = it.peerId,
                limit = it.limit,
                startSequence = it.startMessageSeq
            )
        }

        MessageScene.GROUP -> {
            val groupCache = application.dependencies.resolve<GroupCache>()
            groupCache[it.peerId, true]
                ?: throw MilkyApiException(-404, "Group not found")

            bot.getGroupHistoryMessages(
                groupUin = it.peerId,
                limit = it.limit,
                startSequence = it.startMessageSeq
            )
        }

        else -> throw MilkyApiException(-400, "Unsupported message scene")
    }

    val transformedMessages = historyMessages.messages.mapNotNull { msg ->
        application.transformMessage(msg)
    }

    GetHistoryMessagesOutput(
        messages = transformedMessages,
        nextMessageSeq = historyMessages.nextStartSequence
    )
}

