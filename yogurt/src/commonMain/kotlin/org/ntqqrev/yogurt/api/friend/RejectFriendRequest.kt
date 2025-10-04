package org.ntqqrev.yogurt.api.friend

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.RejectFriendRequestOutput
import org.ntqqrev.yogurt.util.invoke

val RejectFriendRequest = ApiEndpoint.RejectFriendRequest {
    val bot = application.dependencies.resolve<Bot>()

    bot.setFriendRequest(
        initiatorUid = it.initiatorUid,
        accept = false,
        isFiltered = it.isFiltered
    )

    RejectFriendRequestOutput()
}
