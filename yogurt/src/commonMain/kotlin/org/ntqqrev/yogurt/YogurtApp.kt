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
import org.ntqqrev.acidify.event.QRCodeGeneratedEvent
import org.ntqqrev.acidify.event.SessionStoreUpdatedEvent
import org.ntqqrev.acidify.util.UrlSignProvider
import org.ntqqrev.yogurt.util.generateTerminalQRCode
import org.ntqqrev.yogurt.util.logHandler

object YogurtApp {
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
    val qrCodePath = Path("qrcode.png")

    fun start() = runBlocking {
        val bot = Bot.create(
            appInfo = appInfo,
            sessionStore = sessionStore,
            signProvider = signProvider,
            scope = scope,
            minLogLevel = config.logging.coreLogLevel,
            logHandler = logHandler
        )

        scope.launch {
            bot.eventFlow.filterIsInstance<SessionStoreUpdatedEvent>().collect {
                SystemFileSystem.sink(sessionStorePath).buffered().use { source ->
                    Json.encodeToSink(it.sessionStore, source)
                }
            }
        }

        scope.launch {
            bot.eventFlow.filterIsInstance<QRCodeGeneratedEvent>().collect {
                print(generateTerminalQRCode(it.url))
                SystemFileSystem.sink(qrCodePath).buffered().use { sink ->
                    sink.write(it.png)
                }
                println("二维码文件已保存至 ${SystemFileSystem.resolve(qrCodePath)}")
            }
        }

        if (sessionStore.a2.isEmpty()) {
            bot.qrCodeLogin()
        } else {
            bot.tryLogin()
        }

        embeddedServer(
            factory = CIO,
            port = config.httpConfig.port,
            host = config.httpConfig.host
        ) {
            dependencies {
                provide { bot } cleanup {
                    // Throws a JobCancellationException here.
                    // This is a bug tracked as KTOR-8785
                    // and will be fixed at next minor release (3.3.0).
                }
            }
        }.start()
    }
}