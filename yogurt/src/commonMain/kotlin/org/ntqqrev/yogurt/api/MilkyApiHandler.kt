package org.ntqqrev.yogurt.api

import io.ktor.server.routing.*
import org.ntqqrev.milky.ApiEndpoint

abstract class MilkyApiHandler<T : Any, R : Any>(api: ApiEndpoint<T, R>) {
    val path: String = api.path
    abstract suspend fun Route.call(payload: T): R
}