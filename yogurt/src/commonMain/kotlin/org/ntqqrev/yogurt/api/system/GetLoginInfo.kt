package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetLoginInfoOutput
import org.ntqqrev.yogurt.api.MilkyApiHandler

val GetLoginInfo = MilkyApiHandler(ApiEndpoint.GetLoginInfo) {
    val bot: Bot by application.dependencies
    GetLoginInfoOutput(
        uin = bot.uin,
        nickname = "TODO" // todo: resolve nickname
    )
}