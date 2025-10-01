package org.ntqqrev.yogurt.event

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import kotlinx.coroutines.launch
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.util.createHttpClient
import org.ntqqrev.acidify.util.log.Logger
import org.ntqqrev.milky.milkyJsonModule
import org.ntqqrev.yogurt.YogurtApp.config
import org.ntqqrev.yogurt.transform.transformAcidifyEvent

fun Application.configureMilkyEventWebhook() {
    val webhookClient = createHttpClient {
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