package org.ntqqrev.yogurt.api.message

import io.ktor.client.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.SendGroupMessageOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.transform.applySegment
import org.ntqqrev.yogurt.util.GroupCache
import org.ntqqrev.yogurt.util.invoke

val SendGroupMessage = ApiEndpoint.SendGroupMessage {
    val bot = application.dependencies.resolve<Bot>()
    val httpClient = application.dependencies.resolve<HttpClient>()
    val groupCache = application.dependencies.resolve<GroupCache>()
    
    // 检查群聊是否存在
    groupCache[it.groupId, true]
        ?: throw MilkyApiException(-404, "Group not found")
    
    val result = bot.sendGroupMessage(it.groupId) {
        it.message.forEach { segment ->
            applySegment(segment, httpClient)
        }
    }
    
    SendGroupMessageOutput(
        messageSeq = result.sequence,
        time = result.sendTime
    )
}

