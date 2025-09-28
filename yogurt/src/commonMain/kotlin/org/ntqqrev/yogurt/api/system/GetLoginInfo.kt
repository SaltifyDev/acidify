package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetLoginInfoOutput
import org.ntqqrev.yogurt.util.invoke

val GetLoginInfo = ApiEndpoint.GetLoginInfo {
    val bot = application.dependencies.resolve<Bot>()
    GetLoginInfoOutput(
        uin = bot.uin,
        nickname = bot.fetchUserInfoByUid(bot.uid).nickname
    )
}