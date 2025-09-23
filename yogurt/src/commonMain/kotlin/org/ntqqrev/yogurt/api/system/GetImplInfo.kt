package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetImplInfoOutput
import org.ntqqrev.milky.milkyVersion
import org.ntqqrev.yogurt.api.invoke
import org.ntqqrev.yogurt.implName
import org.ntqqrev.yogurt.implVersion

private fun String.toMilkyProtocolOs() = when (this) {
    "Windows" -> "windows"
    "Linux" -> "linux"
    "Mac" -> "macos"
    else -> "linux"
}

val GetImplInfo = ApiEndpoint.GetImplInfo {
    val bot: Bot = application.dependencies.resolve("Bot")
    GetImplInfoOutput(
        implName = implName,
        implVersion = implVersion,
        qqProtocolVersion = bot.appInfo.currentVersion,
        qqProtocolType = bot.appInfo.os.toMilkyProtocolOs(),
        milkyVersion = milkyVersion,
    )
}