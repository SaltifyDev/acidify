package org.ntqqrev.yogurt.api

import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.encodeToJsonElement
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.ApiGeneralResponse
import org.ntqqrev.milky.milkyJsonModule
import org.ntqqrev.yogurt.api.system.GetFriendInfoApi
import org.ntqqrev.yogurt.api.system.GetFriendListApi
import org.ntqqrev.yogurt.api.system.GetLoginInfo

abstract class MilkyApi<T : Any, R : Any>(api: ApiEndpoint<T, R>) {
    val endpoint: String = api.path
    abstract suspend fun Route.call(payload: T): R
}

inline fun <reified T : Any, reified R : Any> MilkyApi(
    api: ApiEndpoint<T, R>,
    crossinline handler: suspend Route.(T) -> R
) =
    object : MilkyApi<T, R>(api) {
        override suspend fun Route.call(payload: T): R = handler(payload)
    }

private inline fun <reified T : Any, reified R : Any> Route.serve(api: MilkyApi<T, R>) {
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
    serve(GetLoginInfo)
    serve(GetFriendListApi)
    serve(GetFriendInfoApi)
}