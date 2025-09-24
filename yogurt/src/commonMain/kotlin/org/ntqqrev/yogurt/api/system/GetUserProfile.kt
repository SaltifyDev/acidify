package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.dependencies
import io.ktor.server.routing.application
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.yogurt.api.invoke
import org.ntqqrev.yogurt.transform.toMilkyOutput

val GetUserProfile = ApiEndpoint.GetUserProfile {
    val bot = application.dependencies.resolve<Bot>()
    bot.fetchUserInfoByUin(it.userId).toMilkyOutput()
}