package org.ntqqrev.yogurt.transform

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.event.*
import org.ntqqrev.acidify.message.MessageScene
import org.ntqqrev.milky.Event
import org.ntqqrev.yogurt.YogurtApp.config

suspend fun Application.transformAcidifyEvent(event: AcidifyEvent): Event? {
    val bot = dependencies.resolve<Bot>()
    return when (event) {
        is BotOfflineEvent -> Event.BotOffline(
            data = Event.BotOffline.Data(
                reason = event.reason
            )
        )

        is MessageReceiveEvent -> {
            if (config.reportSelfMessage || event.message.senderUin != bot.uin) {
                Event.MessageReceive(
                    data = transformMessage(event.message) ?: return null
                )
            } else {
                null
            }
        }

        is MessageRecallEvent -> Event.MessageRecall(
            data = Event.MessageRecall.Data(
                messageScene = event.scene.toMilkyString(),
                peerId = event.peerUin,
                messageSeq = event.messageSeq,
                senderId = event.senderUin,
                operatorId = event.operatorUin,
                displaySuffix = event.displaySuffix
            )
        )

        is FriendRequestEvent -> Event.FriendRequest(
            data = Event.FriendRequest.Data(
                initiatorId = event.initiatorUin,
                initiatorUid = event.initiatorUid,
                comment = event.comment,
                via = event.via
            )
        )

        is GroupJoinRequestEvent -> Event.GroupJoinRequest(
            data = Event.GroupJoinRequest.Data(
                groupId = event.groupUin,
                notificationSeq = event.notificationSeq,
                isFiltered = event.isFiltered,
                initiatorId = event.initiatorUin,
                comment = event.comment
            )
        )

        is GroupInvitedJoinRequestEvent -> Event.GroupInvitedJoinRequest(
            data = Event.GroupInvitedJoinRequest.Data(
                groupId = event.groupUin,
                notificationSeq = event.notificationSeq,
                initiatorId = event.initiatorUin,
                targetUserId = event.targetUserUin
            )
        )

        is GroupInvitationEvent -> Event.GroupInvitation(
            data = Event.GroupInvitation.Data(
                groupId = event.groupUin,
                invitationSeq = event.invitationSeq,
                initiatorId = event.initiatorUin
            )
        )

        is FriendNudgeEvent -> Event.FriendNudge(
            data = Event.FriendNudge.Data(
                userId = event.userUin,
                isSelfSend = event.isSelfSend,
                isSelfReceive = event.isSelfReceive,
                displayAction = event.displayAction,
                displaySuffix = event.displaySuffix,
                displayActionImgUrl = event.displayActionImgUrl
            )
        )

        is FriendFileUploadEvent -> Event.FriendFileUpload(
            data = Event.FriendFileUpload.Data(
                userId = event.userUin,
                fileId = event.fileId,
                fileName = event.fileName,
                fileSize = event.fileSize,
                fileHash = event.fileHash,
                isSelf = event.isSelf
            )
        )

        is GroupAdminChangeEvent -> Event.GroupAdminChange(
            data = Event.GroupAdminChange.Data(
                groupId = event.groupUin,
                userId = event.userUin,
                isSet = event.isSet
            )
        )

        is GroupEssenceMessageChangeEvent -> Event.GroupEssenceMessageChange(
            data = Event.GroupEssenceMessageChange.Data(
                groupId = event.groupUin,
                messageSeq = event.messageSeq,
                isSet = event.isSet
            )
        )

        is GroupMemberIncreaseEvent -> Event.GroupMemberIncrease(
            data = Event.GroupMemberIncrease.Data(
                groupId = event.groupUin,
                userId = event.userUin,
                operatorId = event.operatorUin,
                invitorId = event.invitorUin
            )
        )

        is GroupMemberDecreaseEvent -> Event.GroupMemberDecrease(
            data = Event.GroupMemberDecrease.Data(
                groupId = event.groupUin,
                userId = event.userUin,
                operatorId = event.operatorUin
            )
        )

        is GroupNameChangeEvent -> Event.GroupNameChange(
            data = Event.GroupNameChange.Data(
                groupId = event.groupUin,
                newGroupName = event.newGroupName,
                operatorId = event.operatorUin
            )
        )

        is GroupMessageReactionEvent -> Event.GroupMessageReaction(
            data = Event.GroupMessageReaction.Data(
                groupId = event.groupUin,
                userId = event.userUin,
                messageSeq = event.messageSeq,
                faceId = event.faceId,
                isAdd = event.isAdd
            )
        )

        is GroupMuteEvent -> Event.GroupMute(
            data = Event.GroupMute.Data(
                groupId = event.groupUin,
                userId = event.userUin,
                operatorId = event.operatorUin,
                duration = event.duration
            )
        )

        is GroupWholeMuteEvent -> Event.GroupWholeMute(
            data = Event.GroupWholeMute.Data(
                groupId = event.groupUin,
                operatorId = event.operatorUin,
                isMute = event.isMute
            )
        )

        is GroupNudgeEvent -> Event.GroupNudge(
            data = Event.GroupNudge.Data(
                groupId = event.groupUin,
                senderId = event.senderUin,
                receiverId = event.receiverUin,
                displayAction = event.displayAction,
                displaySuffix = event.displaySuffix,
                displayActionImgUrl = event.displayActionImgUrl
            )
        )

        is GroupFileUploadEvent -> Event.GroupFileUpload(
            data = Event.GroupFileUpload.Data(
                groupId = event.groupUin,
                userId = event.userUin,
                fileId = event.fileId,
                fileName = event.fileName,
                fileSize = event.fileSize
            )
        )

        else -> null
    }
}

fun MessageScene.toMilkyString() = when (this) {
    MessageScene.FRIEND -> "friend"
    MessageScene.GROUP -> "group"
    MessageScene.TEMP -> "temp"
}