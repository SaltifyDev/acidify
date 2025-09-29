@file:OptIn(ExperimentalSerializationApi::class)

package org.ntqqrev.yogurt.util

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.io.encodeToSink
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.event.SessionStoreUpdatedEvent
import org.ntqqrev.acidify.util.log.Logger

val sessionStorePath = Path("session-store.json")

fun Application.configureSessionStoreAutoSave() {
    launch {
        val bot = dependencies.resolve<Bot>()
        val logger = dependencies.resolve<Logger>()
        bot.eventFlow.filterIsInstance<SessionStoreUpdatedEvent>().collect {
            logger.i { "SessionStore 已更新，正在保存至文件..." }
            SystemFileSystem.sink(sessionStorePath).buffered().use { source ->
                Json.encodeToSink(it.sessionStore, source)
            }
        }
    }
}