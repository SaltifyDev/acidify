package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetGroupMemberListOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.invoke

val GetGroupMemberList = ApiEndpoint.GetGroupMemberList {
    val bot = application.dependencies.resolve<Bot>()
    val group = bot.getGroup(it.groupId)
        ?: throw MilkyApiException(-404, "Group not found")
    val members = group.getMembers(forceUpdate = it.noCache)
    GetGroupMemberListOutput(
        members = members.map { member -> member.toMilkyEntity() }
    )
}