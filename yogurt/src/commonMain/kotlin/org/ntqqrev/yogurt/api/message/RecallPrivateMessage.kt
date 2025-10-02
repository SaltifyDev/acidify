package org.ntqqrev.yogurt.api.message

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.RecallPrivateMessageOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.util.FriendCache
import org.ntqqrev.yogurt.util.invoke

val RecallPrivateMessage = ApiEndpoint.RecallPrivateMessage {
    val bot = application.dependencies.resolve<Bot>()
    val friendCache = application.dependencies.resolve<FriendCache>()
    friendCache[it.userId, true]
        ?: throw MilkyApiException(-404, "Friend not found")

    // 获取消息详情
    val messages = bot.getFriendHistoryMessages(
        friendUin = it.userId,
        limit = 1,
        startSequence = it.messageSeq
    )

    val message = messages.messages.firstOrNull()
        ?: throw MilkyApiException(-404, "Message not found")

    // 只能撤回自己发送的消息
    if (message.senderUin != bot.uin) {
        throw MilkyApiException(-403, "Only messages that you sent can be recalled")
    }

    // privateSequence 是好友消息的实际序列号
    val privateSequence = message.sequence

    bot.recallFriendMessage(
        friendUin = it.userId,
        sequence = message.sequence,
        privateSequence = privateSequence,
        timestamp = message.timestamp
    )

    RecallPrivateMessageOutput()
}