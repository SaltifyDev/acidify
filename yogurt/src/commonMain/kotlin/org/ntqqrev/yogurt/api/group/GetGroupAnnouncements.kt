package org.ntqqrev.yogurt.api.group

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetGroupAnnouncementsOutput
import org.ntqqrev.milky.GroupAnnouncementEntity
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.util.GroupCache
import org.ntqqrev.yogurt.util.invoke

val GetGroupAnnouncements = ApiEndpoint.GetGroupAnnouncements {
    val bot = application.dependencies.resolve<Bot>()
    val groupCache = application.dependencies.resolve<GroupCache>()
    groupCache[it.groupId, true]
        ?: throw MilkyApiException(-404, "Group not found")

    val announcements = bot.getGroupAnnouncements(it.groupId)

    GetGroupAnnouncementsOutput(
        announcements = announcements.map { announcement ->
            GroupAnnouncementEntity(
                groupId = announcement.groupUin,
                announcementId = announcement.announcementId,
                userId = announcement.senderId,
                time = announcement.time,
                content = announcement.content,
                imageUrl = announcement.imageUrl
            )
        }
    )
}

