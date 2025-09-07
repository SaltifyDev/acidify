@file:OptIn(ExperimentalSerializationApi::class)

package org.ntqqrev.acidify

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.io.decodeFromSource
import kotlinx.serialization.json.io.encodeToSink
import org.ntqqrev.acidify.common.QrCodeState
import org.ntqqrev.acidify.common.SessionStore
import org.ntqqrev.acidify.event.QrCodeGeneratedEvent
import org.ntqqrev.acidify.event.QrCodeStateQueryEvent
import org.ntqqrev.acidify.event.SessionStoreUpdatedEvent
import kotlin.test.Test
import kotlin.test.assertTrue

class BotTest {
    private val session = if (SystemFileSystem.exists(sessionStorePath)) {
        SystemFileSystem.source(sessionStorePath).buffered().use {
            Json.decodeFromSource<SessionStore>(it)
        }
    } else {
        SessionStore.empty()
    }
    private val bot = runBlocking {
        Bot.create(
            appInfo = defaultSignProvider.getAppInfo()!!,
            sessionStore = session,
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

                    is SessionStoreUpdatedEvent -> {
                        SystemFileSystem.sink(sessionStorePath).buffered().use { sink ->
                            Json.encodeToSink(it.sessionStore, sink)
                        }
                    }
                }
            }
        }
        runBlocking {
            if (session.a2.isEmpty()) {
                bot.qrCodeLogin()
            } else {
                bot.tryLogin()
            }
        }
    }

    @Test
    fun dummyTest() {
        // just a dummy test to make the test framework happy
        assertTrue(true)
    }
}