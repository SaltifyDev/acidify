package org.ntqqrev.yogurt.api.file

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.UploadPrivateFileOutput
import org.ntqqrev.yogurt.util.invoke
import org.ntqqrev.yogurt.util.resolveUri

val UploadPrivateFile = ApiEndpoint.UploadPrivateFile {
    val bot = application.dependencies.resolve<Bot>()

    val fileData = resolveUri(it.fileUri)

    val fileId = bot.uploadPrivateFile(
        friendUin = it.userId,
        fileName = it.fileName,
        fileData = fileData
    )

    UploadPrivateFileOutput(fileId)
}