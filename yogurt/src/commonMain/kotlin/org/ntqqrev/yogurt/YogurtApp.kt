@file:OptIn(ExperimentalSerializationApi::class)

package org.ntqqrev.yogurt

import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.io.decodeFromSource
import kotlinx.serialization.json.io.encodeToSink
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.common.SessionStore
import org.ntqqrev.acidify.util.UrlSignProvider
import org.ntqqrev.milky.milkyJsonModule
import org.ntqqrev.yogurt.api.configureMilkyApiAuth
import org.ntqqrev.yogurt.api.configureMilkyApiHttpRoutes
import org.ntqqrev.yogurt.api.configureMilkyApiLoginProtect
import org.ntqqrev.yogurt.event.configureMilkyEventAuth
import org.ntqqrev.yogurt.event.configureMilkyEventSse
import org.ntqqrev.yogurt.event.configureMilkyEventWebSocket
import org.ntqqrev.yogurt.event.configureMilkyEventWebhook
import org.ntqqrev.yogurt.util.*

object YogurtApp {
    val config = YogurtConfig.loadFromFile()
    val qrCodePath = Path("qrcode.png")

    fun createServer() = embeddedServer(
        factory = CIO,
        port = config.httpConfig.port,
        host = config.httpConfig.host
    ) {
        val signProvider = UrlSignProvider(config.signApiUrl)
        val sessionStore: SessionStore = if (SystemFileSystem.exists(sessionStorePath)) {
            SystemFileSystem.source(sessionStorePath).buffered().use {
                Json.decodeFromSource<SessionStore>(it)
            }
        } else {
            val emptySessionStore = SessionStore.empty()
            SystemFileSystem.sink(sessionStorePath).buffered().use {
                Json.encodeToSink(emptySessionStore, it)
            }
            emptySessionStore
        }
        val appInfo = signProvider.getAppInfo()!!
        val bot = Bot.create(
            appInfo = appInfo,
            sessionStore = sessionStore,
            signProvider = signProvider,
            scope = this@embeddedServer, // application is a CoroutineScope
            minLogLevel = config.logging.coreLogLevel,
            logHandler = logHandler
        )

        val logger = bot.createLogger(this@YogurtApp)

        install(ContentNegotiation) {
            json(milkyJsonModule)
        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(milkyJsonModule)
        }
        install(SSE)

        dependencies {
            provide { bot } cleanup {
                runBlocking { bot.offline() }
            }
            provide { logger }
        }
        configureCacheDeps()

        routing {
            route("/api") {
                if (config.httpConfig.accessToken.isNotEmpty()) {
                    configureMilkyApiAuth()
                }
                configureMilkyApiLoginProtect()
                configureMilkyApiHttpRoutes()
            }
            route("/event") {
                if (config.httpConfig.accessToken.isNotEmpty()) {
                    configureMilkyEventAuth()
                }
                configureMilkyEventWebSocket()
                configureMilkyEventSse()
            }
        }

        monitor.subscribe(ApplicationStarted) {
            if (config.webhookConfig.url.isNotEmpty()) {
                configureMilkyEventWebhook()
            }
            configureQrCodeDisplay()
            configureSessionStoreAutoSave()
            configureEventLogging()

            launch {
                if (bot.sessionStore.a2.isEmpty()) {
                    bot.qrCodeLogin()
                } else {
                    bot.tryLogin()
                }
            }
        }
    }
}