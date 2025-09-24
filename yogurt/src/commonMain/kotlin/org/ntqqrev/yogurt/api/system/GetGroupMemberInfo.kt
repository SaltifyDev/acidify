package org.ntqqrev.yogurt.api.system

import co.touchlab.stately.collections.ConcurrentMutableMap
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.routing.application
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.struct.BotGroupData
import org.ntqqrev.acidify.struct.BotGroupMemberData
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetGroupMemberInfoOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.api.invoke
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.YogurtCache

val GetGroupMemberInfo = ApiEndpoint.GetGroupMemberInfo {
    val bot: Bot = application.dependencies.resolve("Bot")
    val groupCache: YogurtCache<Long, BotGroupData> =
        application.dependencies.resolve("GroupCache")
    val groupMemberMap: ConcurrentMutableMap<Long, YogurtCache<Long, BotGroupMemberData>> =
        application.dependencies.resolve("GroupMemberMap")
    groupCache[it.groupId] ?: throw MilkyApiException(-404, "Group not found")
    val memberCache = groupMemberMap.getOrPut(it.groupId) {
        YogurtCache(bot.scope) { bot.fetchGroupMembers(it.groupId).associateBy { it.uin } }
    }
    val member = memberCache[it.userId, !it.noCache]
        ?: throw MilkyApiException(-404, "Group member not found")
    GetGroupMemberInfoOutput(
        member = member.toMilkyEntity(it.groupId)
    )
}