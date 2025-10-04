package org.ntqqrev.yogurt.api.file

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetGroupFilesOutput
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.invoke

val GetGroupFiles = ApiEndpoint.GetGroupFiles {
    val bot = application.dependencies.resolve<Bot>()

    val result = bot.getGroupFileList(it.groupId, it.parentFolderId, 0)

    GetGroupFilesOutput(
        files = result.files.map { file -> file.toMilkyEntity(it.groupId) },
        folders = result.folders.map { folder -> folder.toMilkyEntity(it.groupId) }
    )
}