package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.yogurt.transform.toMilkyOutput
import org.ntqqrev.yogurt.util.invoke

val GetUserProfile = ApiEndpoint.GetUserProfile {
    val bot = application.dependencies.resolve<Bot>()
    bot.fetchUserInfoByUin(it.userId).toMilkyOutput()
}