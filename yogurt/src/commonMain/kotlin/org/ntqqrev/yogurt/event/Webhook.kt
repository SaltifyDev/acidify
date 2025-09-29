package org.ntqqrev.yogurt.event

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.routing.application
import kotlinx.coroutines.launch
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.util.log.Logger
import org.ntqqrev.milky.milkyJsonModule
import org.ntqqrev.yogurt.YogurtApp.config
import org.ntqqrev.yogurt.transform.transformAcidifyEvent

fun Application.configureMilkyEventWebhook() {
    val webhookClient = HttpClient {
        install(ContentNegotiation) {
            json(milkyJsonModule)
        }
    }
    launch {
        val bot = dependencies.resolve<Bot>()
        val logger = dependencies.resolve<Logger>()
        bot.eventFlow.collect { event ->
            transformAcidifyEvent(event)?.let {
                config.webhookConfig.url.forEach { webhookUrl ->
                    launch {
                        try {
                            webhookClient.post(webhookUrl) {
                                contentType(ContentType.Application.Json)
                                setBody(it)
                            }
                        } catch (e: Exception) {
                            logger.w(e) { "发送事件到 Webhook URL $webhookUrl 失败" }
                        }
                    }
                }
            }
        }
    }
}