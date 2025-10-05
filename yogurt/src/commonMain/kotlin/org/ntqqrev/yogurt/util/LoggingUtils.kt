package org.ntqqrev.yogurt.util

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import kotlinx.coroutines.launch
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.event.*
import org.ntqqrev.acidify.message.MessageScene
import org.ntqqrev.acidify.struct.BotFriendData
import org.ntqqrev.acidify.struct.BotGroupData
import org.ntqqrev.acidify.struct.BotGroupMemberData
import org.ntqqrev.acidify.util.log.LogHandler
import org.ntqqrev.acidify.util.log.LogLevel
import org.ntqqrev.acidify.util.log.Logger
import org.ntqqrev.acidify.util.log.MessageSupplier
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

@Suppress("duplicatedCode")
fun Application.configureEventLogging() {
    launch {
        val bot = dependencies.resolve<Bot>()
        val friendCache = dependencies.resolve<FriendCache>()
        val groupCache = dependencies.resolve<GroupCache>()
        val logger = dependencies.resolve<Logger>()

        fun logAsMessage(supplier: MessageSupplier) {
            (when (config.logging.messageLogLevel) {
                LogLevel.VERBOSE -> logger::v
                LogLevel.DEBUG -> logger::d
                LogLevel.INFO -> logger::i
                LogLevel.WARN -> logger::w
                LogLevel.ERROR -> logger::e
            }).invoke(supplier)
        }

        bot.eventFlow.collect {
            when (it) {
                is MessageReceiveEvent -> {
                    val b = StringBuilder()
                    val isSelfSend = it.message.senderUin == bot.uin
                    b.append(if (isSelfSend) "发送 -> " else "接收 <- ")
                    when (it.message.scene) {
                        MessageScene.FRIEND -> {
                            val friend = friendCache[it.message.peerUin] ?: return@collect
                            b.append("[${friend.displayString}]")
                        }

                        MessageScene.GROUP -> {
                            val group = groupCache[it.message.peerUin] ?: return@collect
                            val memberCache = resolveGroupMemberCache(it.message.peerUin)
                            val member = memberCache?.get(it.message.senderUin) ?: return@collect
                            b.append("[${group.displayString}] [${member.displayString}]")
                        }

                        else -> {
                            b.append("[(${it.message.peerUin})]")
                        }
                    }
                    b.append(" ")
                    b.append(it.message.segments.joinToString(""))

                    logAsMessage { b.toString() }
                }

                is MessageRecallEvent -> {
                    val b = StringBuilder()
                    when (it.messageScene) {
                        "friend" -> {
                            val friend = friendCache[it.peerId] ?: return@collect
                            b.append("[${friend.displayString}] ")
                            if (it.senderUin == bot.uin) {
                                b.append("你撤回了一条消息")
                            } else {
                                b.append("撤回了一条消息")
                            }
                        }

                        "group" -> {
                            val group = groupCache[it.peerId] ?: return@collect
                            val memberCache = resolveGroupMemberCache(it.peerId) ?: return@collect
                            val sender = memberCache[it.senderUin] ?: return@collect
                            val operator = memberCache[it.operatorUin] ?: return@collect

                            b.append("[${group.displayString}] ")
                            b.append("[${sender.displayString}] ")

                            if (it.senderUin == it.operatorUin) {
                                b.append("撤回了一条消息")
                            } else {
                                b.append("的消息被 [${operator.displayString}] 撤回")
                            }
                        }
                    }
                    if (it.displaySuffix.isNotBlank()) {
                        b.append(" (${it.displaySuffix})")
                    }

                    logAsMessage { b.toString() }
                }

                is BotOfflineEvent -> {
                    logger.e { "Bot 已离线，原因：${it.reason}" }
                }

                is FriendNudgeEvent -> {
                    val b = StringBuilder()
                    val friend = friendCache[it.userUin] ?: return@collect

                    if (it.isSelfSend) {
                        b.append("你")
                        b.append(it.displayAction)
                        if (it.isSelfReceive) {
                            b.append("自己")
                        } else {
                            b.append(friend.displayString)
                        }
                        b.append(it.displaySuffix)
                    } else {
                        b.append(friend.displayString)
                        b.append(it.displayAction)
                        if (it.isSelfReceive) {
                            b.append("你")
                        } else {
                            b.append("自己")
                        }
                        b.append(it.displaySuffix)
                    }

                    logAsMessage { b.toString() }
                }

                is FriendRequestEvent -> {
                    logAsMessage { "收到来自 ${it.initiatorUin} 的好友请求，附加信息：${it.comment}" }
                }

                is GroupAdminChangeEvent -> {
                    val b = StringBuilder()
                    val group = groupCache[it.groupUin] ?: return@collect
                    val memberCache = resolveGroupMemberCache(it.groupUin)
                    val operator = memberCache?.get(it.userUin) ?: return@collect

                    b.append("[${group.displayString}] [${operator.displayString}] ")
                    b.append(if (it.isSet) "被设置为" else "被取消")
                    b.append("管理员")

                    logAsMessage { b.toString() }
                }

                is GroupEssenceMessageChangeEvent -> {
                    val b = StringBuilder()
                    val group = groupCache[it.groupUin] ?: return@collect

                    b.append("[${group.displayString}] ")
                    b.append("消息 #${it.messageSeq} ")
                    b.append(if (it.isSet) "被设置为" else "被取消")
                    b.append("精华消息")

                    logAsMessage { b.toString() }
                }

                is GroupInvitationEvent -> {
                    logAsMessage { "${it.initiatorUin} 邀请自己加入群 ${it.groupUin}" }
                }

                is GroupInvitedJoinRequestEvent -> {
                    val b = StringBuilder()
                    val group = groupCache[it.groupUin] ?: return@collect
                    val memberCache = resolveGroupMemberCache(it.groupUin)
                    val initiator = memberCache?.get(it.initiatorUin) ?: return@collect

                    b.append("[${group.displayString}] ")
                    b.append("[${initiator.displayString}] ")
                    b.append("邀请 ${it.targetUserUin} 加入群聊")

                    logAsMessage { b.toString() }
                }

                is GroupJoinRequestEvent -> {
                    val b = StringBuilder()
                    val group = groupCache[it.groupUin] ?: return@collect

                    b.append("[${group.displayString}] ")
                    b.append("收到 ${it.initiatorUin} 的入群申请，附加信息：${it.comment} ")

                    logAsMessage { b.toString() }
                }

                is GroupMemberIncreaseEvent -> {
                    val b = StringBuilder()
                    val group = groupCache[it.groupUin] ?: return@collect

                    b.append("[${group.displayString}] ")
                    b.append("${it.userUin} ")

                    when {
                        it.operatorUin != null -> {
                            val memberCache = resolveGroupMemberCache(it.groupUin) ?: return@collect
                            val operator = memberCache[it.operatorUin!!] ?: return@collect
                            b.append("被 [${operator.displayString}] 同意加入群聊")
                        }

                        it.invitorUin != null -> {
                            val memberCache = resolveGroupMemberCache(it.groupUin) ?: return@collect
                            val invitor = memberCache[it.invitorUin!!] ?: return@collect
                            b.append("被 [${invitor.displayString}] 邀请加入群聊")
                        }

                        else -> {
                            b.append("加入了群聊")
                        }
                    }

                    logAsMessage { b.toString() }
                }

                is GroupMemberDecreaseEvent -> {
                    val b = StringBuilder()
                    val group = groupCache[it.groupUin] ?: return@collect

                    b.append("[${group.displayString}] ")
                    b.append("${it.userUin} ")

                    if (it.operatorUin != null && it.operatorUin != it.userUin) {
                        val memberCache = resolveGroupMemberCache(it.groupUin) ?: return@collect
                        val operator = memberCache[it.operatorUin!!] ?: return@collect
                        b.append("被 [${operator.displayString}] 移出群聊")
                    } else {
                        b.append("退出了群聊")
                    }

                    logAsMessage { b.toString() }
                }

                is GroupNameChangeEvent -> {
                    val b = StringBuilder()
                    val group = groupCache[it.groupUin] ?: return@collect
                    val memberCache = resolveGroupMemberCache(it.groupUin) ?: return@collect
                    val operator = memberCache[it.operatorUin] ?: return@collect

                    b.append("[${group.displayString}] ")
                    b.append("[${operator.displayString}] ")
                    b.append("将群名称修改为：${it.newGroupName}")

                    logAsMessage { b.toString() }
                }

                is GroupMessageReactionEvent -> {
                    val b = StringBuilder()
                    val group = groupCache[it.groupUin] ?: return@collect
                    val memberCache = resolveGroupMemberCache(it.groupUin) ?: return@collect
                    val user = memberCache[it.userUin] ?: return@collect

                    b.append("[${group.displayString}] ")
                    b.append("[${user.displayString}] ")

                    if (it.isAdd) {
                        b.append("对消息 #${it.messageSeq} 添加了表情回应: ${it.faceId}")
                    } else {
                        b.append("取消了对消息 #${it.messageSeq} 的表情回应: ${it.faceId}")
                    }

                    logAsMessage { b.toString() }
                }

                is GroupMuteEvent -> {
                    val b = StringBuilder()
                    val group = groupCache[it.groupUin] ?: return@collect
                    val memberCache = resolveGroupMemberCache(it.groupUin) ?: return@collect
                    val user = memberCache[it.userUin] ?: return@collect
                    val operator = memberCache[it.operatorUin] ?: return@collect

                    b.append("[${group.displayString}] ")
                    b.append("[${user.displayString}] ")

                    if (it.duration == 0) {
                        b.append("被 [${operator.displayString}] 解除禁言")
                    } else {
                        b.append("被 [${operator.displayString}] 禁言 ${it.duration} 秒")
                    }

                    logAsMessage { b.toString() }
                }

                is GroupWholeMuteEvent -> {
                    val b = StringBuilder()
                    val group = groupCache[it.groupUin] ?: return@collect
                    val memberCache = resolveGroupMemberCache(it.groupUin) ?: return@collect
                    val operator = memberCache[it.operatorUin] ?: return@collect

                    b.append("[${group.displayString}] ")
                    b.append("[${operator.displayString}] ")

                    if (it.isMute) {
                        b.append("开启了全员禁言")
                    } else {
                        b.append("关闭了全员禁言")
                    }

                    logAsMessage { b.toString() }
                }

                is GroupNudgeEvent -> {
                    val b = StringBuilder()
                    val group = groupCache[it.groupUin] ?: return@collect
                    val memberCache = resolveGroupMemberCache(it.groupUin) ?: return@collect
                    val sender = memberCache[it.senderUin] ?: return@collect
                    val receiver = memberCache[it.receiverUin] ?: return@collect

                    b.append("[${group.displayString}] ")
                    b.append(sender.displayString)
                    b.append(" ")
                    b.append(it.displayAction)
                    b.append(receiver.displayString)
                    b.append(" ")
                    b.append(it.displaySuffix)

                    logAsMessage { b.toString() }
                }
            }
        }
    }
}