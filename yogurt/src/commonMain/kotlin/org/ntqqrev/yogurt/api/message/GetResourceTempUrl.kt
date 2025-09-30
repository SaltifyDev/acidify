package org.ntqqrev.yogurt.api.message

import io.ktor.server.plugins.di.dependencies
import io.ktor.server.routing.application
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetResourceTempUrlOutput
import org.ntqqrev.yogurt.util.invoke

val GetResourceTempUrl = ApiEndpoint.GetResourceTempUrl {
    val bot = application.dependencies.resolve<Bot>()
    GetResourceTempUrlOutput(
        url = bot.getDownloadUrl(it.resourceId)
    )
}