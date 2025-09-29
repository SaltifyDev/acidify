package org.ntqqrev.yogurt.util

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import kotlinx.coroutines.launch
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.event.MessageReceiveEvent
import org.ntqqrev.acidify.message.MessageScene
import org.ntqqrev.acidify.struct.BotFriendData
import org.ntqqrev.acidify.struct.BotGroupData
import org.ntqqrev.acidify.struct.BotGroupMemberData
import org.ntqqrev.acidify.util.log.LogHandler
import org.ntqqrev.acidify.util.log.LogLevel
import org.ntqqrev.acidify.util.log.Logger
import org.ntqqrev.yogurt.YogurtApp.config

expect val logHandler: LogHandler

private val BotFriendData.displayName
    get() = remark.ifBlank { nickname }

private val BotGroupMemberData.displayName
    get() = card.ifBlank { nickname }

private val BotFriendData.displayString: String
    get() = "$displayName ($uin)"

private val BotGroupData.displayString: String
    get() = "$name ($uin)"

private val BotGroupMemberData.displayString: String
    get() = "$displayName ($uin)"

fun Application.configureEventLogging() {
    launch {
        val bot = dependencies.resolve<Bot>()
        val friendCache = dependencies.resolve<FriendCache>()
        val groupCache = dependencies.resolve<GroupCache>()
        val logger = dependencies.resolve<Logger>()

        bot.eventFlow.collect {
            when (it) {
                is MessageReceiveEvent -> {
                    (when (config.logging.messageLogLevel) {
                        LogLevel.VERBOSE -> logger::v
                        LogLevel.DEBUG -> logger::d
                        LogLevel.INFO -> logger::i
                        LogLevel.WARN -> logger::w
                        LogLevel.ERROR -> logger::e
                    }) {
                        val b = StringBuilder()
                        val isSelfSend = it.message.senderUin == bot.uin
                        b.append(if (isSelfSend) "发送消息 " else "接收消息 ")
                        when (it.message.scene) {
                            MessageScene.FRIEND -> {
                                val friend = friendCache[it.message.peerUin]
                                if (friend != null) {
                                    b.append("[${friend.displayString}]")
                                } else {
                                    b.append("[(${it.message.peerUin})]")
                                }
                            }

                            MessageScene.GROUP -> {
                                val group = groupCache[it.message.peerUin]
                                if (group != null) {
                                    b.append("[${group.displayString}]")
                                    val memberCache = resolveGroupMemberCache(it.message.peerUin)
                                    val member = memberCache?.get(it.message.senderUin)
                                    if (member != null) {
                                        b.append(" [${member.displayString}]")
                                    } else {
                                        b.append(" [(${it.message.senderUin})]")
                                    }
                                } else {
                                    b.append("[(${it.message.peerUin})] [(${it.message.senderUin})]")
                                }
                            }

                            else -> {
                                b.append("[(${it.message.peerUin})]")
                            }
                        }
                        b.append(" ")
                        b.append(it.message.segments.joinToString(""))
                        b.toString()
                    }
                }
            }
        }
    }
}