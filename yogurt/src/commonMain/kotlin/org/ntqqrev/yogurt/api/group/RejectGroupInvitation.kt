package org.ntqqrev.yogurt.api.group

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.RejectGroupInvitationOutput
import org.ntqqrev.yogurt.util.invoke

val RejectGroupInvitation = ApiEndpoint.RejectGroupInvitation {
    val bot = application.dependencies.resolve<Bot>()

    bot.setGroupInvitation(
        groupUin = it.groupId,
        invitationSeq = it.invitationSeq,
        accept = false
    )

    RejectGroupInvitationOutput()
}
