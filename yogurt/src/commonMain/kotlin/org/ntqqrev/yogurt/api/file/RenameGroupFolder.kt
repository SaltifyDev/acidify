package org.ntqqrev.yogurt.api.file

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.RenameGroupFolderOutput
import org.ntqqrev.yogurt.util.invoke

val RenameGroupFolder = ApiEndpoint.RenameGroupFolder {
    val bot = application.dependencies.resolve<Bot>()

    bot.renameGroupFolder(it.groupId, it.folderId, it.newFolderName)

    RenameGroupFolderOutput()
}
