package org.ntqqrev.yogurt.util

import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.event.AcidifyEvent
import org.ntqqrev.acidify.event.MessageReceiveEvent

fun Application.configurePreprocessedEventFlow(bot: Bot) {
    val preprocessedEventFlow = bot.eventFlow.map {
        if (it is MessageReceiveEvent && it.message.extraInfo != null) {
            resolveGroupMemberCache(it.message.peerUin)?.let { cache ->
                cache[it.message.senderUin]?.let { oldInfo ->
                    cache[it.message.senderUin] = oldInfo.copy(
                        nickname = it.message.extraInfo!!.nick,
                        card = it.message.extraInfo!!.groupCard,
                        specialTitle = it.message.extraInfo!!.specialTitle
                    )
                }
            }
        }
        it
    }.shareIn(bot, SharingStarted.Lazily)
    dependencies {
        provide<PreprocessedEventFlow> { preprocessedEventFlow }
    }
}

typealias PreprocessedEventFlow = SharedFlow<AcidifyEvent>