package org.ntqqrev.yogurt.api.group

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.SendGroupAnnouncementOutput
import org.ntqqrev.yogurt.api.MilkyApiException

import org.ntqqrev.yogurt.util.invoke

val SendGroupAnnouncement = ApiEndpoint.SendGroupAnnouncement {
    val bot = application.dependencies.resolve<Bot>()
    bot.getGroup(it.groupId)
        ?: throw MilkyApiException(-404, "Group not found")

    bot.sendGroupAnnouncement(it.groupId, it.content, it.imageUri)

    SendGroupAnnouncementOutput()
}

