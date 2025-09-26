package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetCsrfTokenOutput
import org.ntqqrev.yogurt.api.invoke

val GetCsrfToken = ApiEndpoint.GetCsrfToken {
    val bot = application.dependencies.resolve<Bot>()
    GetCsrfTokenOutput(
        csrfToken = bot.getCsrfToken().toString()
    )
}