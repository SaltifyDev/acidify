package org.ntqqrev.acidify.event.internal

import io.ktor.http.parseUrl
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.event.*
import org.ntqqrev.acidify.internal.packet.message.PushMsg
import org.ntqqrev.acidify.internal.packet.message.PushMsgType
import org.ntqqrev.acidify.internal.packet.message.extra.*
import org.ntqqrev.acidify.message.BotIncomingMessage.Companion.parseMessage
import org.ntqqrev.acidify.message.BotIncomingSegment
import org.ntqqrev.acidify.message.MessageScene
import org.ntqqrev.acidify.pb.invoke
import org.ntqqrev.acidify.struct.BotGroupNotification
import org.ntqqrev.acidify.struct.RequestState

internal object MsgPushSignal : AbstractSignal("trpc.msg.olpush.OlPushService.MsgPush") {
    @Suppress("duplicatedCode")
    override suspend fun parse(bot: Bot, payload: ByteArray): List<AcidifyEvent> {
        val commonMsg = PushMsg(payload).get { message }
        val contentHead = commonMsg.get { contentHead }
        val routingHead = commonMsg.get { routingHead }
        val msgBody = commonMsg.get { messageBody }
        val msgContent = msgBody.get { msgContent }
        val pushMsgType = PushMsgType.from(contentHead.get { type })

        when (pushMsgType) {
            PushMsgType.FriendMessage,
            PushMsgType.FriendRecordMessage,
            PushMsgType.FriendFileMessage,
            PushMsgType.GroupMessage -> {
                val msg = bot.parseMessage(commonMsg) ?: return listOf()
                val mutList = mutableListOf<AcidifyEvent>(MessageReceiveEvent(msg))

                msg.segments.filterIsInstance<BotIncomingSegment.LightApp>()
                    .firstOrNull()
                    ?.takeIf { it.appName == "com.tencent.qun.invite" || it.appName == "com.tencent.tuwen.lua" }
                    ?.let {
                        Json.decodeFromString<JsonElement>(it.jsonPayload)
                            .jsonObject["data"]
                            ?.jsonObject["meta"]
                            ?.jsonObject["news"]
                            ?.jsonObject["jumpUrl"]
                            ?.jsonPrimitive?.content
                    }?.let {
                        parseUrl(it)
                    }?.takeIf {
                        it.parameters["groupcode"] != null && it.parameters["msgseq"] != null
                    }?.let {
                        mutList += GroupInvitationEvent(
                            groupUin = it.parameters["groupcode"]!!.toLong(),
                            invitationSeq = it.parameters["msgseq"]!!.toLong(),
                            initiatorUin = msg.senderUin,
                            initiatorUid = msg.senderUid
                        )
                    }

                return mutList
            }

            else -> {}
        }

        if (msgContent.isEmpty()) {
            return listOf()
        }

        return when (pushMsgType) {
            PushMsgType.GroupJoinRequest -> {
                val content = GroupJoinRequest(msgContent)
                val groupUin = content.get { groupUin }
                val memberUid = content.get { memberUid }
                val memberUin = bot.getUinByUid(memberUid)

                val recentNotifications =
                    bot.getGroupNotifications(isFiltered = false, count = 30).first +
                            bot.getGroupNotifications(isFiltered = true, count = 10).first

                recentNotifications
                    .filterIsInstance<BotGroupNotification.JoinRequest>()
                    .find {
                        it.groupUin == groupUin &&
                                it.initiatorUin == memberUin &&
                                it.state == RequestState.PENDING
                    }
                    ?.let {
                        listOf(
                            GroupJoinRequestEvent(
                                groupUin = groupUin,
                                notificationSeq = it.notificationSeq,
                                isFiltered = it.isFiltered,
                                initiatorUin = memberUin,
                                initiatorUid = memberUid,
                                comment = it.comment
                            )
                        )
                    }
                    ?: listOf()
            }

            PushMsgType.GroupInvitedJoinRequest -> {
                val content = GroupInvitedJoinRequest(msgContent)
                if (content.get { command } == 87) {
                    val info = content.get { info } ?: return listOf()
                    val inner = info.get { inner } ?: return listOf()
                    val groupUin = inner.get { groupUin }
                    val targetUid = inner.get { targetUid }
                    val invitorUid = inner.get { invitorUid }
                    val targetUin = bot.getUinByUid(targetUid)
                    val invitorUin = bot.getUinByUid(invitorUid)

                    val recentNotifications =
                        bot.getGroupNotifications(isFiltered = false, count = 30).first +
                                bot.getGroupNotifications(isFiltered = true, count = 10).first

                    recentNotifications
                        .filterIsInstance<BotGroupNotification.InvitedJoinRequest>()
                        .find {
                            it.groupUin == groupUin &&
                                    it.initiatorUin == invitorUin &&
                                    it.targetUserUin == targetUin &&
                                    it.state == RequestState.PENDING
                        }
                        ?.let {
                            listOf(
                                GroupInvitedJoinRequestEvent(
                                    groupUin = groupUin,
                                    notificationSeq = it.notificationSeq,
                                    initiatorUin = invitorUin,
                                    initiatorUid = invitorUid,
                                    targetUserUin = targetUin,
                                    targetUserUid = targetUid
                                )
                            )
                        }
                        ?: listOf()
                } else {
                    listOf()
                }
            }

            PushMsgType.GroupAdminChange -> {
                val content = GroupAdminChange(msgContent)
                val groupUin = content.get { groupUin }
                val body = content.get { body } ?: return listOf()
                val (targetUid, isSet) = if (body.get { set } != null) {
                    body.get { set }!!.get { targetUid } to true
                } else if (body.get { unset } != null) {
                    body.get { unset }!!.get { targetUid } to false
                } else {
                    return listOf()
                }
                val targetUin = bot.getUinByUid(targetUid)
                listOf(
                    GroupAdminChangeEvent(
                        groupUin = groupUin,
                        userUin = targetUin,
                        userUid = targetUid,
                        isSet = isSet
                    )
                )
            }

            PushMsgType.GroupMemberIncrease -> {
                val content = GroupMemberChange(msgContent)
                val groupUin = content.get { groupUin }
                val memberUid = content.get { memberUid }
                val memberUin = bot.getUinByUid(memberUid)
                val operatorInfoBytes = content.get { operatorInfo }
                val operatorUid = operatorInfoBytes.toString()
                val operatorUin = bot.getUinByUid(operatorUid)

                when (GroupMemberChange.IncreaseType.from(content.get { type })) {
                    GroupMemberChange.IncreaseType.APPROVE -> listOf(
                        GroupMemberIncreaseEvent(
                            groupUin = groupUin,
                            userUin = memberUin,
                            userUid = memberUid,
                            operatorUin = operatorUin,
                            operatorUid = operatorUid,
                            invitorUin = null,
                            invitorUid = null
                        )
                    )

                    GroupMemberChange.IncreaseType.INVITE -> listOf(
                        GroupMemberIncreaseEvent(
                            groupUin = groupUin,
                            userUin = memberUin,
                            userUid = memberUid,
                            operatorUin = null,
                            operatorUid = null,
                            invitorUin = operatorUin,
                            invitorUid = operatorUid
                        )
                    )

                    else -> listOf()
                }
            }

            PushMsgType.GroupMemberDecrease -> {
                val content = GroupMemberChange(msgContent)
                val groupUin = content.get { groupUin }
                val memberUid = content.get { memberUid }
                val memberUin = bot.getUinByUid(memberUid)
                val changeType = content.get { type }
                val operatorInfoBytes = content.get { operatorInfo }
                val operatorUid =
                    if (changeType == GroupMemberChange.DecreaseType.KICK_SELF.value && operatorInfoBytes != null) {
                        GroupMemberChange.OperatorInfo(operatorInfoBytes).get { body }?.get { uid }
                    } else {
                        operatorInfoBytes?.decodeToString()
                    }
                val operatorUin = operatorUid?.let { bot.getUinByUid(it) }

                listOf(
                    GroupMemberDecreaseEvent(
                        groupUin = groupUin,
                        userUin = memberUin,
                        userUid = memberUid,
                        operatorUin = operatorUin,
                        operatorUid = operatorUid
                    )
                )
            }

            PushMsgType.Event0x210 -> {
                val subType = contentHead.get { subType }
                when (subType) {
                    35 -> { // FriendRequest
                        val content = FriendRequest(msgContent)
                        val body = content.get { body } ?: return listOf()
                        val fromUid = body.get { fromUid }
                        val fromUin = routingHead.get { fromUin }
                        val comment = body.get { message }
                        val via = body.get { via } ?: FriendRequestExtractVia(msgContent)
                            .get { this.body }?.get { via } ?: ""

                        listOf(
                            FriendRequestEvent(
                                initiatorUin = fromUin,
                                initiatorUid = fromUid,
                                comment = comment,
                                via = via
                            )
                        )
                    }

                    290 -> { // FriendGrayTip (FriendNudge)
                        val content = GeneralGrayTip(msgContent)
                        if (content.get { bizType } == 12) {
                            val templateParamsMap = content.get { templateParams }.associate {
                                it.get { key } to it.get { value }
                            }
                            val fromUin = routingHead.get { fromUin }
                            val uin1 = templateParamsMap["uin_str1"]?.toLongOrNull() ?: return listOf()
                            val uin2 = templateParamsMap["uin_str2"]?.toLongOrNull() ?: return listOf()
                            val action = templateParamsMap["action_str"]
                                ?: templateParamsMap["alt_str1"] ?: ""
                            val actionImgUrl = templateParamsMap["action_img_url"] ?: ""
                            val suffix = templateParamsMap["suffix_str"] ?: ""

                            listOf(
                                FriendNudgeEvent(
                                    userUin = fromUin,
                                    userUid = bot.getUidByUin(fromUin),
                                    isSelfSend = uin1 == bot.uin,
                                    isSelfReceive = uin2 == bot.uin,
                                    displayAction = action,
                                    displaySuffix = suffix,
                                    displayActionImgUrl = actionImgUrl
                                )
                            )
                        } else {
                            listOf()
                        }
                    }

                    138, 139 -> { // FriendRecall, FriendSelfRecall
                        val content = FriendRecall(msgContent)
                        val body = content.get { body } ?: return listOf()
                        val fromUid = body.get { fromUid }
                        val toUid = body.get { toUid }
                        val sequence = body.get { sequence }.toLong()
                        val displaySuffix = body.get { tipInfo }?.get { tip } ?: ""
                        val fromUin = bot.getUinByUid(fromUid)
                        val toUin = bot.getUinByUid(toUid)

                        listOf(
                            MessageRecallEvent(
                                scene = MessageScene.FRIEND,
                                peerUin = if (subType == 0x122) toUin else fromUin,
                                messageSeq = sequence,
                                senderUin = fromUin,
                                senderUid = fromUid,
                                operatorUin = fromUin,
                                operatorUid = fromUid,
                                displaySuffix = displaySuffix
                            )
                        )
                    }

                    else -> listOf()
                }
            }

            PushMsgType.Event0x2DC -> {
                val subType = contentHead.get { subType }
                when (subType) {
                    12 -> { // GroupMute
                        val content = GroupMute(msgContent)
                        val groupUin = content.get { groupUin }
                        val operatorUid = content.get { operatorUid }
                        val operatorUin = bot.getUinByUid(operatorUid)
                        val info = content.get { info } ?: return listOf()
                        val state = info.get { state } ?: return listOf()
                        val targetUid = state.get { targetUid }
                        val duration = state.get { duration }

                        if (targetUid != null) {
                            val targetUin = bot.getUinByUid(targetUid)
                            listOf(
                                GroupMuteEvent(
                                    groupUin = groupUin,
                                    userUin = targetUin,
                                    userUid = targetUid,
                                    operatorUin = operatorUin,
                                    operatorUid = operatorUid,
                                    duration = duration
                                )
                            )
                        } else {
                            listOf(
                                GroupWholeMuteEvent(
                                    groupUin = groupUin,
                                    operatorUin = operatorUin,
                                    operatorUid = operatorUid,
                                    isMute = duration != 0
                                )
                            )
                        }
                    }

                    20 -> { // GroupGrayTip (may contain GroupNudge)
                        val wrapper = GroupGeneral0x2DC.Body(
                            GroupGeneral0x2DC(msgContent).body
                        )
                        val content = wrapper.get { generalGrayTip } ?: return listOf()

                        if (content.get { bizType } == 12) {
                            val templateParamsMap = content.get { templateParams }.associate {
                                it.get { key } to it.get { value }
                            }
                            val groupUin = wrapper.get { groupUin }
                            val uin1 = templateParamsMap["uin_str1"]?.toLongOrNull() ?: return listOf()
                            val uin2 = templateParamsMap["uin_str2"]?.toLongOrNull() ?: return listOf()
                            val action = templateParamsMap["action_str"]
                                ?: templateParamsMap["alt_str1"] ?: ""
                            val actionImgUrl = templateParamsMap["action_img_url"] ?: ""
                            val suffix = templateParamsMap["suffix_str"] ?: ""

                            listOf(
                                GroupNudgeEvent(
                                    groupUin = groupUin,
                                    senderUin = uin1,
                                    senderUid = bot.getUidByUin(uin1, groupUin),
                                    receiverUin = uin2,
                                    receiverUid = bot.getUidByUin(uin2, groupUin),
                                    displayAction = action,
                                    displaySuffix = suffix,
                                    displayActionImgUrl = actionImgUrl
                                )
                            )
                        } else {
                            listOf()
                        }
                    }

                    21 -> { // GroupEssenceMessageChange
                        val wrapper = GroupGeneral0x2DC.Body(
                            GroupGeneral0x2DC(msgContent).body
                        )
                        val content = wrapper.get { essenceMessageChange } ?: return listOf()
                        val groupUin = content.get { groupUin }
                        val msgSeq = content.get { msgSequence }.toLong()
                        // val operatorUin = content.get { operatorUin }
                        val isSet = content.get { setFlag } == GroupEssenceMessageChange.SetFlag.ADD.value

                        listOf(
                            GroupEssenceMessageChangeEvent(
                                groupUin = groupUin,
                                messageSeq = msgSeq,
                                isSet = isSet
                            )
                        )
                    }

                    17 -> { // GroupRecall
                        val wrapper = GroupGeneral0x2DC.Body(
                            GroupGeneral0x2DC(msgContent).body
                        )
                        val content = wrapper.get { recall } ?: return listOf()
                        val groupUin = wrapper.get { groupUin }
                        val operatorUid = content.get { operatorUid }
                        val operatorUin = bot.getUinByUid(operatorUid)
                        val displaySuffix = content.get { tipInfo }?.get { tip } ?: ""

                        content.get { recallMessages }.map { recall ->
                            val authorUid = recall.get { authorUid }
                            val authorUin = bot.getUinByUid(authorUid)
                            MessageRecallEvent(
                                scene = MessageScene.GROUP,
                                peerUin = groupUin,
                                messageSeq = recall.get { sequence }.toLong(),
                                senderUin = authorUin,
                                senderUid = authorUid,
                                operatorUin = operatorUin,
                                operatorUid = operatorUid,
                                displaySuffix = displaySuffix
                            )
                        }
                    }

                    16 -> { // SubType16 (may contain GroupReaction or GroupNameChange)
                        val wrapper = GroupGeneral0x2DC.Body(
                            GroupGeneral0x2DC(msgContent).body
                        )
                        val field13 = wrapper.get { field13 }

                        when (field13) {
                            35 -> { // GroupReaction
                                val content = wrapper.get { reaction } ?: return listOf()
                                val data = content.get { data } ?: return listOf()
                                val dataMiddle = data.get { this.data } ?: return listOf()
                                val target = dataMiddle.get { target } ?: return listOf()
                                val dataInner = dataMiddle.get { this.data } ?: return listOf()

                                val groupUin = wrapper.get { groupUin }
                                val msgSeq = target.get { sequence }.toLong()
                                val operatorUid = dataInner.get { operatorUid }
                                val operatorUin = bot.getUinByUid(operatorUid)
                                val faceId = dataInner.get { code }
                                val isAdd = dataInner.get { type } == 1

                                listOf(
                                    GroupMessageReactionEvent(
                                        groupUin = groupUin,
                                        userUin = operatorUin,
                                        userUid = operatorUid,
                                        messageSeq = msgSeq,
                                        faceId = faceId,
                                        isAdd = isAdd
                                    )
                                )
                            }

                            12 -> { // GroupNameChange
                                val eventParam = wrapper.get { eventParam } ?: return listOf()
                                val content = GroupNameChange(eventParam)
                                val groupUin = wrapper.get { groupUin }
                                val newName = content.get { name }
                                val operatorUid = wrapper.get { operatorUid } ?: return listOf()
                                val operatorUin = bot.getUinByUid(operatorUid)

                                listOf(
                                    GroupNameChangeEvent(
                                        groupUin = groupUin,
                                        newGroupName = newName,
                                        operatorUin = operatorUin,
                                        operatorUid = operatorUid
                                    )
                                )
                            }

                            else -> listOf()
                        }
                    }

                    else -> listOf()
                }
            }

            else -> listOf()
        }
    }
}