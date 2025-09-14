package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.yogurt.api.MilkyApi
import org.ntqqrev.yogurt.protocol.GetLoginInfoInput
import org.ntqqrev.yogurt.protocol.GetLoginInfoOutput

object GetLoginInfoApi : MilkyApi<GetLoginInfoInput, GetLoginInfoOutput>("get_login_info") {
    override suspend fun Route.call(payload: GetLoginInfoInput): GetLoginInfoOutput {
        val bot: Bot by application.dependencies
        return GetLoginInfoOutput(
            uin = bot.uin,
            nickname = "TODO" // todo: resolve nickname
        )
    }
}