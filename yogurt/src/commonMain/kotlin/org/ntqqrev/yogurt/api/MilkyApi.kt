package org.ntqqrev.yogurt.api

import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.encodeToJsonElement
import org.ntqqrev.yogurt.api.system.GetLoginInfoApi
import org.ntqqrev.yogurt.protocol.ApiGeneralResponse
import org.ntqqrev.yogurt.protocol.milkyJsonModule

abstract class MilkyApi<T : Any, R>(val endpoint: String) {
    abstract suspend fun Route.call(payload: T): R
}

private inline fun <reified T : Any, reified R> Route.serve(api: MilkyApi<T, R>) {
    post("/${api.endpoint}") {
        try {
            val payload = call.receive<T>()
            call.respond(
                try {
                    val result = with(api) { call(payload) }
                    ApiGeneralResponse(
                        status = "ok",
                        retcode = 0,
                        data = milkyJsonModule.encodeToJsonElement(result)
                    )
                } catch (e: MilkyApiException) {
                    ApiGeneralResponse(
                        status = "failed",
                        retcode = e.retcode,
                        message = e.message
                    )
                } catch (e: Exception) {
                    ApiGeneralResponse(
                        status = "failed",
                        retcode = -500,
                        message = "Internal error: ${e.message}"
                    )
                }
            )
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

fun Route.configureMilkyApi() {
    serve(GetLoginInfoApi)
}