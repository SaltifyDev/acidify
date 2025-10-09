package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetFriendInfoOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.invoke

val GetFriendInfo = ApiEndpoint.GetFriendInfo {
    val bot = application.dependencies.resolve<Bot>()
    val friend = bot.getFriend(it.userId, forceUpdate = it.noCache)
        ?: throw MilkyApiException(-404, "Friend not found")
    GetFriendInfoOutput(
        friend = friend.toMilkyEntity()
    )
}