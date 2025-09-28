package org.ntqqrev.yogurt.api

import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiGeneralResponse

fun Route.configureMilkyApiLoginProtect() {
    val protectNotLoggedIn = createRouteScopedPlugin("ProtectNotLoggedIn") {
        onCall { call ->
            val bot = this@configureMilkyApiLoginProtect.application.dependencies.resolve<Bot>()
            if (!bot.isLoggedIn) {
                call.respond(
                    ApiGeneralResponse(
                        status = "failed",
                        retcode = -403,
                        message = "Bot is not logged in"
                    )
                )
                return@onCall
            }
        }
    }
    install(protectNotLoggedIn)
}