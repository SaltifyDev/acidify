package org.ntqqrev.yogurt.api.file

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.DeleteGroupFileOutput
import org.ntqqrev.yogurt.util.invoke

val DeleteGroupFile = ApiEndpoint.DeleteGroupFile {
    val bot = application.dependencies.resolve<Bot>()

    bot.deleteGroupFile(it.groupId, it.fileId)

    DeleteGroupFileOutput()
}
