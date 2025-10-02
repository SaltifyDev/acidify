package org.ntqqrev.yogurt.api.group

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.SetGroupMemberCardOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.util.GroupCache
import org.ntqqrev.yogurt.util.invoke

val SetGroupMemberCard = ApiEndpoint.SetGroupMemberCard {
    val bot = application.dependencies.resolve<Bot>()
    val groupCache = application.dependencies.resolve<GroupCache>()
    groupCache[it.groupId, true]
        ?: throw MilkyApiException(-404, "Group not found")

    bot.setGroupMemberCard(it.groupId, it.userId, it.card)

    SetGroupMemberCardOutput()
}

