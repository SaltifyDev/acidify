package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetFriendListOutput
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.invoke

val GetFriendList = ApiEndpoint.GetFriendList {
    val bot = application.dependencies.resolve<Bot>()
    val friends = bot.getFriends(forceUpdate = it.noCache)
    GetFriendListOutput(
        friends = friends.map { friend -> friend.toMilkyEntity() }
    )
}