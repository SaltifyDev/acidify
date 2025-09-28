package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetCookiesOutput
import org.ntqqrev.yogurt.util.invoke

val GetCookies = ApiEndpoint.GetCookies {
    val bot = application.dependencies.resolve<Bot>()
    return@GetCookies GetCookiesOutput(
        cookies = bot.getCookies(it.domain).entries
            .joinToString("; ") { "${it.key}=${it.value}" }
    )
}