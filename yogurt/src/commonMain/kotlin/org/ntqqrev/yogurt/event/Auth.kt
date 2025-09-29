package org.ntqqrev.yogurt.event

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ntqqrev.yogurt.YogurtApp.config

fun Route.configureMilkyEventAuth() {
    val auth = createRouteScopedPlugin("EventAuth") {
        onCall { call ->
            if (
                call.request.headers["Authorization"] != "Bearer ${config.httpConfig.accessToken}" &&
                call.request.queryParameters["access_token"] != config.httpConfig.accessToken
            ) {
                call.respond(HttpStatusCode.Unauthorized)
                return@onCall
            }
        }
    }
    install(auth)
}