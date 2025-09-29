package org.ntqqrev.yogurt.util

import io.ktor.server.routing.*
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.yogurt.api.MilkyApiHandler

inline operator fun <reified T : Any, reified R : Any> ApiEndpoint<T, R>.invoke(
    crossinline handler: suspend Route.(T) -> R
) = object : MilkyApiHandler<T, R>(this) {
    override suspend fun Route.call(payload: T): R = handler(payload)
}