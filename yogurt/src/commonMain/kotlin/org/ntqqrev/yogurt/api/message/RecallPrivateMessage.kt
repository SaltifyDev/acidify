package org.ntqqrev.yogurt.api.message

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.RecallPrivateMessageOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.util.invoke

val RecallPrivateMessage = ApiEndpoint.RecallPrivateMessage {
    val bot = application.dependencies.resolve<Bot>()
    bot.getFriend(it.userId)
        ?: throw MilkyApiException(-404, "Friend not found")

    bot.recallFriendMessage(
        friendUin = it.userId,
        sequence = it.messageSeq
    )

    RecallPrivateMessageOutput()
}