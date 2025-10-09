package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetGroupMemberInfoOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.invoke

val GetGroupMemberInfo = ApiEndpoint.GetGroupMemberInfo {
    val bot = application.dependencies.resolve<Bot>()
    val group = bot.getGroup(it.groupId)
        ?: throw MilkyApiException(-404, "Group not found")
    val member = group.getMember(it.userId, forceUpdate = it.noCache)
        ?: throw MilkyApiException(-404, "Group member not found")
    GetGroupMemberInfoOutput(
        member = member.toMilkyEntity()
    )
}