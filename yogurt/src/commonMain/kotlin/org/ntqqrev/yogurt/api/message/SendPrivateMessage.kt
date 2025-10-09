package org.ntqqrev.yogurt.api.message

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.message.MessageScene
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.SendPrivateMessageOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.transform.YogurtMessageBuildingContext
import org.ntqqrev.yogurt.transform.applySegment
import org.ntqqrev.yogurt.util.invoke

val SendPrivateMessage = ApiEndpoint.SendPrivateMessage {
    val bot = application.dependencies.resolve<Bot>()
    
    // 检查好友是否存在
    bot.getFriend(it.userId)
        ?: throw MilkyApiException(-404, "Friend not found")
    
    val result = bot.sendFriendMessage(it.userId) {
        with(
            YogurtMessageBuildingContext(
                application,
                this,
                MessageScene.FRIEND,
                it.userId
            )
        ) {
            it.message.forEach { segment ->
                applySegment(segment)
            }
        }
    }
    
    SendPrivateMessageOutput(
        messageSeq = result.sequence,
        time = result.sendTime
    )
}

