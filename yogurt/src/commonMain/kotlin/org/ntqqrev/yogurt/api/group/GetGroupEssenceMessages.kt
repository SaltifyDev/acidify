package org.ntqqrev.yogurt.api.group

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetGroupEssenceMessagesOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.transform.transformEssenceMessage
import org.ntqqrev.yogurt.util.GroupCache
import org.ntqqrev.yogurt.util.invoke

val GetGroupEssenceMessages = ApiEndpoint.GetGroupEssenceMessages {
    val bot = application.dependencies.resolve<Bot>()
    val groupCache = application.dependencies.resolve<GroupCache>()
    groupCache[it.groupId, true]
        ?: throw MilkyApiException(-404, "Group not found")

    val essenceMessageResult = bot.getGroupEssenceMessages(
        groupUin = it.groupId,
        pageIndex = it.pageIndex,
        pageSize = it.pageSize
    )

    GetGroupEssenceMessagesOutput(
        messages = essenceMessageResult.messages.map {
            application.async { application.transformEssenceMessage(it) }
        }.awaitAll(),
        isEnd = essenceMessageResult.isEnd
    )
}