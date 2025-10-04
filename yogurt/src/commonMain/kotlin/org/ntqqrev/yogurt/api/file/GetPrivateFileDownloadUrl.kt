package org.ntqqrev.yogurt.api.file

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetPrivateFileDownloadUrlOutput
import org.ntqqrev.yogurt.util.invoke

val GetPrivateFileDownloadUrl = ApiEndpoint.GetPrivateFileDownloadUrl {
    val bot = application.dependencies.resolve<Bot>()

    val url = bot.getPrivateFileDownloadUrl(it.userId, it.fileId, it.fileHash)

    GetPrivateFileDownloadUrlOutput(url)
}