package org.ntqqrev.acidify

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.io.encodeToSink
import org.ntqqrev.acidify.common.QrCodeState
import org.ntqqrev.acidify.common.SessionStore
import org.ntqqrev.acidify.event.QrCodeGeneratedEvent
import org.ntqqrev.acidify.event.QrCodeStateQueryEvent
import kotlin.test.Test

class LoginTest {
    private val currentSessionStore = SessionStore.empty()
    private val bot = runBlocking {
        Bot.create(
            appInfo = defaultSignProvider.getAppInfo()!!,
            sessionStore = currentSessionStore,
            signProvider = defaultSignProvider,
            scope = defaultScope
        )
    }

    init {
        defaultScope.launch {
            bot.eventFlow.collect {
                when (it) {
                    is QrCodeGeneratedEvent -> {
                        println("QR Code URL: ${it.url}")
                    }

                    is QrCodeStateQueryEvent -> {
                        println("QR Code State: ${it.state}")
                        if (it.state == QrCodeState.CONFIRMED) {
                            println("Login confirmed by user.")
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun qrCodeLogin() = runBlocking {
        bot.qrCodeLogin()
        SystemFileSystem.sink(sessionStorePath).buffered().use {
            Json.encodeToSink(currentSessionStore, it)
        }
    }
}