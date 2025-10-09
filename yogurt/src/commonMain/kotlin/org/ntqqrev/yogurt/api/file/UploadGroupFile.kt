package org.ntqqrev.yogurt.api.file

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.UploadGroupFileOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.util.invoke
import org.ntqqrev.yogurt.util.resolveUri

val UploadGroupFile = ApiEndpoint.UploadGroupFile {
    val bot = application.dependencies.resolve<Bot>()
    bot.getGroup(it.groupId) ?: throw MilkyApiException(-404, "Group not found")

    // 解析文件 URI 并获取文件数据
    val fileData = resolveUri(it.fileUri)

    // 调用 Bot API 上传群文件
    val fileId = bot.uploadGroupFile(
        groupUin = it.groupId,
        fileName = it.fileName,
        fileData = fileData,
        parentFolderId = it.parentFolderId
    )

    UploadGroupFileOutput(fileId)
}