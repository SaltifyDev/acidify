package org.ntqqrev.yogurt.api.file

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.DeleteGroupFolderOutput
import org.ntqqrev.yogurt.util.invoke

val DeleteGroupFolder = ApiEndpoint.DeleteGroupFolder {
    val bot = application.dependencies.resolve<Bot>()

    bot.deleteGroupFolder(it.groupId, it.folderId)

    DeleteGroupFolderOutput()
}
