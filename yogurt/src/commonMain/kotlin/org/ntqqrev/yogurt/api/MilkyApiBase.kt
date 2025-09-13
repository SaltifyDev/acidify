package org.ntqqrev.yogurt.api

import io.ktor.server.plugins.di.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.encodeToJsonElement
import org.ntqqrev.acidify.Bot
import org.ntqqrev.yogurt.api.system.routeGetLoginInfoApi
import org.ntqqrev.yogurt.protocol.ApiGeneralResponse
import org.ntqqrev.yogurt.protocol.milkyJsonModule

typealias MilkyApiHandler<T, R> = suspend Bot.(T) -> R

inline fun <reified T : Any, reified R : Any> routeMilkyApi(
    endpoint: String,
    crossinline handler: MilkyApiHandler<T, R>
): Route.() -> Unit = {
    post("/$endpoint") {
        try {
            val payload = call.receive<T>()
            val bot: Bot by application.dependencies
            try {
                val result = bot.handler(payload)
                call.respond(
                    ApiGeneralResponse(
                        status = "ok",
                        retcode = 0,
                        data = milkyJsonModule.encodeToJsonElement(result)
                    )
                )
            } catch (e: MilkyApiException) {
                call.respond(
                    ApiGeneralResponse(
                        status = "failed",
                        retcode = e.retcode,
                        message = e.message
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    ApiGeneralResponse(
                        status = "failed",
                        retcode = -500,
                        message = "Internal error: ${e.message}"
                    )
                )
            }
        } catch (e: Exception) {
            call.respond(
                ApiGeneralResponse(
                    status = "failed",
                    retcode = -400,
                    message = "Bad request: ${e.message}"
                )
            )
        }
    }
}

val apiRoutingList = listOf(
    routeGetLoginInfoApi,
)