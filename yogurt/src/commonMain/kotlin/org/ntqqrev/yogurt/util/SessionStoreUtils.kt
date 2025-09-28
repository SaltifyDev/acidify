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
import org.ntqqrev.yogurt.YogurtApp.scope

val sessionStorePath = Path("session-store.json")

fun Application.configureSessionStoreAutoSave() {
    scope.launch {
        val bot = dependencies.resolve<Bot>()
        bot.eventFlow.filterIsInstance<SessionStoreUpdatedEvent>().collect {
            SystemFileSystem.sink(sessionStorePath).buffered().use { source ->
                Json.encodeToSink(it.sessionStore, source)
            }
        }
    }
}