package org.ntqqrev.yogurt.api.file

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.MoveGroupFileOutput
import org.ntqqrev.yogurt.util.invoke

val MoveGroupFile = ApiEndpoint.MoveGroupFile {
    val bot = application.dependencies.resolve<Bot>()

    bot.moveGroupFile(it.groupId, it.fileId, it.parentFolderId, it.targetFolderId)

    MoveGroupFileOutput()
}
