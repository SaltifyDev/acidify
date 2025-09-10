@file:OptIn(ExperimentalSerializationApi::class)

package org.ntqqrev.yogurt

import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.di.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.io.decodeFromSource
import kotlinx.serialization.json.io.encodeToSink
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.common.SessionStore
import org.ntqqrev.acidify.event.SessionStoreUpdatedEvent
import org.ntqqrev.acidify.util.UrlSignProvider
import org.ntqqrev.acidify.util.createLogger

object YogurtApp {
    val logger = createLogger(this)
    val config = YogurtConfig.loadFromFile()
    val signProvider = UrlSignProvider(config.signApiUrl)
    val appInfo = signProvider.getAppInfo()!!
    val sessionStorePath = Path("session-store.json")
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
    val scope = CoroutineScope(Dispatchers.IO)

    fun start() = runBlocking {
        embeddedServer(
            factory = CIO,
            port = config.httpConfig.port,
            host = config.httpConfig.host
        ) {
            val bot = Bot.create(appInfo, sessionStore, signProvider, scope)
            dependencies {
                provide<Bot> { bot }
            }
            scope.launch {
                bot.eventFlow.filterIsInstance(SessionStoreUpdatedEvent::class).collect {
                    SystemFileSystem.sink(sessionStorePath).buffered().use { source ->
                        Json.encodeToSink(it.sessionStore, source)
                    }
                }
            }
            if (sessionStore.a2.isEmpty()) {
                bot.qrCodeLogin()
            } else {
                bot.tryLogin()
            }
        }.start()

        logger.i { "服务器已在 ${config.httpConfig.host}:${config.httpConfig.port} 启动" }

        delay(Long.MAX_VALUE)
    }
}