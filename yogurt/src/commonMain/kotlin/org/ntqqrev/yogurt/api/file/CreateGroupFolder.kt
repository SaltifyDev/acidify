package org.ntqqrev.yogurt.api.file

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.CreateGroupFolderOutput
import org.ntqqrev.yogurt.util.invoke

val CreateGroupFolder = ApiEndpoint.CreateGroupFolder {
    val bot = application.dependencies.resolve<Bot>()

    val folderId = bot.createGroupFolder(it.groupId, it.folderName)

    CreateGroupFolderOutput(folderId)
}
