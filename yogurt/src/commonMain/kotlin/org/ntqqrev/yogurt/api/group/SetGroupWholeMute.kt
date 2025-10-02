package org.ntqqrev.yogurt.api.group

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.SetGroupWholeMuteOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.util.GroupCache
import org.ntqqrev.yogurt.util.invoke

val SetGroupWholeMute = ApiEndpoint.SetGroupWholeMute {
    val bot = application.dependencies.resolve<Bot>()
    val groupCache = application.dependencies.resolve<GroupCache>()
    groupCache[it.groupId, true]
        ?: throw MilkyApiException(-404, "Group not found")

    bot.setGroupWholeMute(it.groupId, it.isMute)

    SetGroupWholeMuteOutput()
}

