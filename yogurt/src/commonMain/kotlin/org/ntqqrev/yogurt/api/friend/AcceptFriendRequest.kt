package org.ntqqrev.yogurt.api.friend

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.AcceptFriendRequestOutput
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.yogurt.util.invoke

val AcceptFriendRequest = ApiEndpoint.AcceptFriendRequest {
    val bot = application.dependencies.resolve<Bot>()

    bot.setFriendRequest(
        initiatorUid = it.initiatorUid,
        accept = true,
        isFiltered = it.isFiltered
    )

    AcceptFriendRequestOutput()
}
