package org.ntqqrev.yogurt.api.file

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetGroupFileDownloadUrlOutput
import org.ntqqrev.yogurt.util.invoke

val GetGroupFileDownloadUrl = ApiEndpoint.GetGroupFileDownloadUrl {
    val bot = application.dependencies.resolve<Bot>()

    val url = bot.getGroupFileDownloadUrl(it.groupId, it.fileId)

    GetGroupFileDownloadUrlOutput(url)
}