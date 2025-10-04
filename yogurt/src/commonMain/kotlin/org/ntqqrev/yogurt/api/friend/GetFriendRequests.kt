package org.ntqqrev.yogurt.api.friend

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetFriendRequestsOutput
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.invoke

val GetFriendRequests = ApiEndpoint.GetFriendRequests {
    val bot = application.dependencies.resolve<Bot>()

    val requests = bot.getFriendRequests(it.isFiltered, it.limit)

    GetFriendRequestsOutput(
        requests = requests.map { req -> req.toMilkyEntity() }
    )
}
