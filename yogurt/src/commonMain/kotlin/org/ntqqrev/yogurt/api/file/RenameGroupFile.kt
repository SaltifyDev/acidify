package org.ntqqrev.yogurt.api.file

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.RenameGroupFileOutput
import org.ntqqrev.yogurt.util.invoke

val RenameGroupFile = ApiEndpoint.RenameGroupFile {
    val bot = application.dependencies.resolve<Bot>()

    bot.renameGroupFile(it.groupId, it.fileId, it.parentFolderId, it.newFileName)

    RenameGroupFileOutput()
}
