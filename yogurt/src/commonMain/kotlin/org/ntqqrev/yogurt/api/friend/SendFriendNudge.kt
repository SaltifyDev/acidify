package org.ntqqrev.yogurt.api.friend

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.SendFriendNudgeOutput
import org.ntqqrev.yogurt.util.invoke

val SendFriendNudge = ApiEndpoint.SendFriendNudge {
    val bot = application.dependencies.resolve<Bot>()

    bot.sendFriendNudge(it.userId, it.isSelf)

    SendFriendNudgeOutput()
}