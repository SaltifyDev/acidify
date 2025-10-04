package org.ntqqrev.yogurt.api.group

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetGroupNotificationsOutput
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.invoke

val GetGroupNotifications = ApiEndpoint.GetGroupNotifications {
    val bot = application.dependencies.resolve<Bot>()

    val (notifications, nextSeq) = bot.getGroupNotifications(
        startSequence = it.startNotificationSeq,
        isFiltered = it.isFiltered,
        count = it.limit
    )

    GetGroupNotificationsOutput(
        notifications = notifications.map { it.toMilkyEntity() },
        nextNotificationSeq = nextSeq
    )
}
