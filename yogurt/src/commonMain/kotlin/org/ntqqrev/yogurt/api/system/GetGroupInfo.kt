package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetGroupInfoOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.invoke

val GetGroupInfo = ApiEndpoint.GetGroupInfo {
    val bot = application.dependencies.resolve<Bot>()
    val group = bot.getGroup(it.groupId, forceUpdate = it.noCache)
        ?: throw MilkyApiException(-404, "Group not found")
    GetGroupInfoOutput(
        group = group.toMilkyEntity()
    )
}