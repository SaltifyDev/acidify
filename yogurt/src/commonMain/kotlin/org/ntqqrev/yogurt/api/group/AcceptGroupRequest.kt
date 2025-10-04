package org.ntqqrev.yogurt.api.group

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.AcceptGroupRequestOutput
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.yogurt.transform.toEventType
import org.ntqqrev.yogurt.util.invoke

val AcceptGroupRequest = ApiEndpoint.AcceptGroupRequest {
    val bot = application.dependencies.resolve<Bot>()

    bot.setGroupRequest(
        groupUin = it.groupId,
        sequence = it.notificationSeq,
        eventType = it.notificationType.toEventType(),
        accept = true,
        isFiltered = it.isFiltered
    )

    AcceptGroupRequestOutput()
}
