package org.ntqqrev.yogurt.api.group

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.KickGroupMemberOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.util.invoke

val KickGroupMember = ApiEndpoint.KickGroupMember {
    val bot = application.dependencies.resolve<Bot>()
    bot.getGroup(it.groupId)
        ?: throw MilkyApiException(-404, "Group not found")

    bot.kickGroupMember(it.groupId, it.userId, it.rejectAddRequest)

    KickGroupMemberOutput()
}

