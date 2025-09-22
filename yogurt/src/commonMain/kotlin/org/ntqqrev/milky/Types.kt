// Auto-generated file
@file:OptIn(ExperimentalSerializationApi::class)

package org.ntqqrev.milky

import kotlinx.serialization.Serializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*

const val milkyVersion = "1.0"
const val milkyPackageVersion = "1.0.0-draft.19"

val milkyJsonModule = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

// ####################################
// Common Structs
// ####################################

/** 事件 */
@Serializable
@JsonClassDiscriminator("event_type")
sealed class Event {
    /** 机器人离线事件 */
    @Serializable
    @SerialName("bot_offline")
    class BotOffline(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 下线原因 */
            @SerialName("reason") val reason: String,
        )
    }

    /** 消息接收事件 */
    @Serializable
    @SerialName("message_receive")
    class MessageReceive(
        val data: IncomingMessage
    ) : Event()

    /** 消息撤回事件 */
    @Serializable
    @SerialName("message_recall")
    class MessageRecall(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 消息场景 */
            @SerialName("message_scene") val messageScene: String,
            /** 好友 QQ 号或群号 */
            @SerialName("peer_id") val peerId: Long,
            /** 消息序列号 */
            @SerialName("message_seq") val messageSeq: Long,
            /** 被撤回的消息的发送者 QQ 号 */
            @SerialName("sender_id") val senderId: Long,
            /** 操作者 QQ 号 */
            @SerialName("operator_id") val operatorId: Long,
            /** 撤回提示的后缀文本 */
            @SerialName("display_suffix") val displaySuffix: String,
        )
    }

    /** 好友请求事件 */
    @Serializable
    @SerialName("friend_request")
    class FriendRequest(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 申请好友的用户 QQ 号 */
            @SerialName("initiator_id") val initiatorId: Long,
            /** 用户 UID */
            @SerialName("initiator_uid") val initiatorUid: String,
            /** 申请附加信息 */
            @SerialName("comment") val comment: String,
            /** 申请来源 */
            @SerialName("via") val via: String,
        )
    }

    /** 入群请求事件 */
    @Serializable
    @SerialName("group_join_request")
    class GroupJoinRequest(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 群号 */
            @SerialName("group_id") val groupId: Long,
            /** 请求对应的通知序列号 */
            @SerialName("notification_seq") val notificationSeq: Long,
            /** 请求是否被过滤（发起自风险账户） */
            @SerialName("is_filtered") val isFiltered: Boolean,
            /** 申请入群的用户 QQ 号 */
            @SerialName("initiator_id") val initiatorId: Long,
            /** 申请附加信息 */
            @SerialName("comment") val comment: String,
        )
    }

    /** 群成员邀请他人入群请求事件 */
    @Serializable
    @SerialName("group_invited_join_request")
    class GroupInvitedJoinRequest(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 群号 */
            @SerialName("group_id") val groupId: Long,
            /** 请求对应的通知序列号 */
            @SerialName("notification_seq") val notificationSeq: Long,
            /** 邀请者 QQ 号 */
            @SerialName("initiator_id") val initiatorId: Long,
            /** 被邀请者 QQ 号 */
            @SerialName("target_user_id") val targetUserId: Long,
        )
    }

    /** 他人邀请自身入群事件 */
    @Serializable
    @SerialName("group_invitation")
    class GroupInvitation(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 群号 */
            @SerialName("group_id") val groupId: Long,
            /** 邀请序列号 */
            @SerialName("invitation_seq") val invitationSeq: Long,
            /** 邀请者 QQ 号 */
            @SerialName("initiator_id") val initiatorId: Long,
        )
    }

    /** 好友戳一戳事件 */
    @Serializable
    @SerialName("friend_nudge")
    class FriendNudge(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 好友 QQ 号 */
            @SerialName("user_id") val userId: Long,
            /** 是否是自己发送的戳一戳 */
            @SerialName("is_self_send") val isSelfSend: Boolean,
            /** 是否是自己接收的戳一戳 */
            @SerialName("is_self_receive") val isSelfReceive: Boolean,
            /** 戳一戳提示的动作文本 */
            @SerialName("display_action") val displayAction: String,
            /** 戳一戳提示的后缀文本 */
            @SerialName("display_suffix") val displaySuffix: String,
            /** 戳一戳提示的动作图片 URL，用于取代动作提示文本 */
            @SerialName("display_action_img_url") val displayActionImgUrl: String,
        )
    }

    /** 好友文件上传事件 */
    @Serializable
    @SerialName("friend_file_upload")
    class FriendFileUpload(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 好友 QQ 号 */
            @SerialName("user_id") val userId: Long,
            /** 文件 ID */
            @SerialName("file_id") val fileId: String,
            /** 文件名称 */
            @SerialName("file_name") val fileName: String,
            /** 文件大小（字节） */
            @SerialName("file_size") val fileSize: Long,
            /** 文件的 TriSHA1 哈希值 */
            @SerialName("file_hash") val fileHash: String,
            /** 是否是自己发送的文件 */
            @SerialName("is_self") val isSelf: Boolean,
        )
    }

    /** 群管理员变更事件 */
    @Serializable
    @SerialName("group_admin_change")
    class GroupAdminChange(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 群号 */
            @SerialName("group_id") val groupId: Long,
            /** 发生变更的用户 QQ 号 */
            @SerialName("user_id") val userId: Long,
            /** 是否被设置为管理员，`false` 表示被取消管理员 */
            @SerialName("is_set") val isSet: Boolean,
        )
    }

    /** 群精华消息变更事件 */
    @Serializable
    @SerialName("group_essence_message_change")
    class GroupEssenceMessageChange(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 群号 */
            @SerialName("group_id") val groupId: Long,
            /** 发生变更的消息序列号 */
            @SerialName("message_seq") val messageSeq: Long,
            /** 是否被设置为精华，`false` 表示被取消精华 */
            @SerialName("is_set") val isSet: Boolean,
        )
    }

    /** 群成员增加事件 */
    @Serializable
    @SerialName("group_member_increase")
    class GroupMemberIncrease(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 群号 */
            @SerialName("group_id") val groupId: Long,
            /** 发生变更的用户 QQ 号 */
            @SerialName("user_id") val userId: Long,
            /** 管理员 QQ 号，如果是管理员同意入群 */
            @SerialName("operator_id") val operatorId: Long? = null,
            /** 邀请者 QQ 号，如果是邀请入群 */
            @SerialName("invitor_id") val invitorId: Long? = null,
        )
    }

    /** 群成员减少事件 */
    @Serializable
    @SerialName("group_member_decrease")
    class GroupMemberDecrease(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 群号 */
            @SerialName("group_id") val groupId: Long,
            /** 发生变更的用户 QQ 号 */
            @SerialName("user_id") val userId: Long,
            /** 管理员 QQ 号，如果是管理员踢出 */
            @SerialName("operator_id") val operatorId: Long? = null,
        )
    }

    /** 群名称变更事件 */
    @Serializable
    @SerialName("group_name_change")
    class GroupNameChange(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 群号 */
            @SerialName("group_id") val groupId: Long,
            /** 新的群名称 */
            @SerialName("new_group_name") val newGroupName: String,
            /** 操作者 QQ 号 */
            @SerialName("operator_id") val operatorId: Long,
        )
    }

    /** 群消息表情回应事件 */
    @Serializable
    @SerialName("group_message_reaction")
    class GroupMessageReaction(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 群号 */
            @SerialName("group_id") val groupId: Long,
            /** 发送回应者 QQ 号 */
            @SerialName("user_id") val userId: Long,
            /** 消息序列号 */
            @SerialName("message_seq") val messageSeq: Long,
            /** 表情 ID */
            @SerialName("face_id") val faceId: String,
            /** 是否为添加，`false` 表示取消回应 */
            @SerialName("is_add") val isAdd: Boolean,
        )
    }

    /** 群禁言事件 */
    @Serializable
    @SerialName("group_mute")
    class GroupMute(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 群号 */
            @SerialName("group_id") val groupId: Long,
            /** 发生变更的用户 QQ 号 */
            @SerialName("user_id") val userId: Long,
            /** 操作者 QQ 号 */
            @SerialName("operator_id") val operatorId: Long,
            /** 禁言时长（秒），为 0 表示取消禁言 */
            @SerialName("duration") val duration: Int,
        )
    }

    /** 群全体禁言事件 */
    @Serializable
    @SerialName("group_whole_mute")
    class GroupWholeMute(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 群号 */
            @SerialName("group_id") val groupId: Long,
            /** 操作者 QQ 号 */
            @SerialName("operator_id") val operatorId: Long,
            /** 是否全员禁言，`false` 表示取消全员禁言 */
            @SerialName("is_mute") val isMute: Boolean,
        )
    }

    /** 群戳一戳事件 */
    @Serializable
    @SerialName("group_nudge")
    class GroupNudge(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 群号 */
            @SerialName("group_id") val groupId: Long,
            /** 发送者 QQ 号 */
            @SerialName("sender_id") val senderId: Long,
            /** 接收者 QQ 号 */
            @SerialName("receiver_id") val receiverId: Long,
            /** 戳一戳提示的动作文本 */
            @SerialName("display_action") val displayAction: String,
            /** 戳一戳提示的后缀文本 */
            @SerialName("display_suffix") val displaySuffix: String,
            /** 戳一戳提示的动作图片 URL，用于取代动作提示文本 */
            @SerialName("display_action_img_url") val displayActionImgUrl: String,
        )
    }

    /** 群文件上传事件 */
    @Serializable
    @SerialName("group_file_upload")
    class GroupFileUpload(
        val data: Data
    ) : Event() {
        @Serializable
        class Data(
            /** 群号 */
            @SerialName("group_id") val groupId: Long,
            /** 发送者 QQ 号 */
            @SerialName("user_id") val userId: Long,
            /** 文件 ID */
            @SerialName("file_id") val fileId: String,
            /** 文件名称 */
            @SerialName("file_name") val fileName: String,
            /** 文件大小（字节） */
            @SerialName("file_size") val fileSize: Long,
        )
    }
}

/** 好友实体 */
@Serializable
class FriendEntity(
    /** 用户 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 用户昵称 */
    @SerialName("nickname") val nickname: String,
    /** 用户性别 */
    @SerialName("sex") val sex: String,
    /** 用户 QID */
    @SerialName("qid") val qid: String,
    /** 好友备注 */
    @SerialName("remark") val remark: String,
    /** 好友分组 */
    @SerialName("category") val category: FriendCategoryEntity,
)

/** 好友分组实体 */
@Serializable
class FriendCategoryEntity(
    /** 好友分组 ID */
    @SerialName("category_id") val categoryId: Int,
    /** 好友分组名称 */
    @SerialName("category_name") val categoryName: String,
)

/** 群实体 */
@Serializable
class GroupEntity(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 群名称 */
    @SerialName("group_name") val groupName: String,
    /** 群成员数量 */
    @SerialName("member_count") val memberCount: Int,
    /** 群容量 */
    @SerialName("max_member_count") val maxMemberCount: Int,
)

/** 群成员实体 */
@Serializable
class GroupMemberEntity(
    /** 用户 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 用户昵称 */
    @SerialName("nickname") val nickname: String,
    /** 用户性别 */
    @SerialName("sex") val sex: String,
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 成员备注 */
    @SerialName("card") val card: String,
    /** 专属头衔 */
    @SerialName("title") val title: String,
    /** 群等级，注意和 QQ 等级区分 */
    @SerialName("level") val level: Int,
    /** 权限等级 */
    @SerialName("role") val role: String,
    /** 入群时间，Unix 时间戳（秒） */
    @SerialName("join_time") val joinTime: Long,
    /** 最后发言时间，Unix 时间戳（秒） */
    @SerialName("last_sent_time") val lastSentTime: Long,
    /** 禁言结束时间，Unix 时间戳（秒） */
    @SerialName("shut_up_end_time") val shutUpEndTime: Long? = null,
)

/** 群公告实体 */
@Serializable
class GroupAnnouncementEntity(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 公告 ID */
    @SerialName("announcement_id") val announcementId: String,
    /** 发送者 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** Unix 时间戳（秒） */
    @SerialName("time") val time: Long,
    /** 公告内容 */
    @SerialName("content") val content: String,
    /** 公告图片 URL */
    @SerialName("image_url") val imageUrl: String? = null,
)

/** 群文件实体 */
@Serializable
class GroupFileEntity(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 文件 ID */
    @SerialName("file_id") val fileId: String,
    /** 文件名称 */
    @SerialName("file_name") val fileName: String,
    /** 父文件夹 ID */
    @SerialName("parent_folder_id") val parentFolderId: String,
    /** 文件大小（字节） */
    @SerialName("file_size") val fileSize: Long,
    /** 上传时的 Unix 时间戳（秒） */
    @SerialName("uploaded_time") val uploadedTime: Long,
    /** 过期时的 Unix 时间戳（秒） */
    @SerialName("expire_time") val expireTime: Long? = null,
    /** 上传者 QQ 号 */
    @SerialName("uploader_id") val uploaderId: Long,
    /** 下载次数 */
    @SerialName("downloaded_times") val downloadedTimes: Int,
)

/** 群文件夹实体 */
@Serializable
class GroupFolderEntity(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 文件夹 ID */
    @SerialName("folder_id") val folderId: String,
    /** 父文件夹 ID */
    @SerialName("parent_folder_id") val parentFolderId: String,
    /** 文件夹名称 */
    @SerialName("folder_name") val folderName: String,
    /** 创建时的 Unix 时间戳（秒） */
    @SerialName("created_time") val createdTime: Long,
    /** 最后修改时的 Unix 时间戳（秒） */
    @SerialName("last_modified_time") val lastModifiedTime: Long,
    /** 创建者 QQ 号 */
    @SerialName("creator_id") val creatorId: Long,
    /** 文件数量 */
    @SerialName("file_count") val fileCount: Int,
)

/** 好友请求实体 */
@Serializable
class FriendRequest(
    /** 请求发起时的 Unix 时间戳（秒） */
    @SerialName("time") val time: Long,
    /** 请求发起者 QQ 号 */
    @SerialName("initiator_id") val initiatorId: Long,
    /** 请求发起者 UID */
    @SerialName("initiator_uid") val initiatorUid: String,
    /** 目标用户 QQ 号 */
    @SerialName("target_user_id") val targetUserId: Long,
    /** 目标用户 UID */
    @SerialName("target_user_uid") val targetUserUid: String,
    /** 请求状态 */
    @SerialName("state") val state: String,
    /** 申请附加信息 */
    @SerialName("comment") val comment: String,
    /** 申请来源 */
    @SerialName("via") val via: String,
    /** 请求是否被过滤（发起自风险账户） */
    @SerialName("is_filtered") val isFiltered: Boolean,
)

/** 群通知实体 */
@Serializable
@JsonClassDiscriminator("type")
sealed class GroupNotification {
    /** 用户入群请求 */
    @Serializable
    @SerialName("join_request")
    class JoinRequest(
        /** 群号 */
        @SerialName("group_id") val groupId: Long,
        /** 通知序列号 */
        @SerialName("notification_seq") val notificationSeq: Long,
        /** 请求是否被过滤（发起自风险账户） */
        @SerialName("is_filtered") val isFiltered: Boolean,
        /** 发起者 QQ 号 */
        @SerialName("initiator_id") val initiatorId: Long,
        /** 请求状态 */
        @SerialName("state") val state: String,
        /** 处理请求的管理员 QQ 号 */
        @SerialName("operator_id") val operatorId: Long? = null,
        /** 入群请求附加信息 */
        @SerialName("comment") val comment: String,
    )

    /** 群管理员变更通知 */
    @Serializable
    @SerialName("admin_change")
    class AdminChange(
        /** 群号 */
        @SerialName("group_id") val groupId: Long,
        /** 通知序列号 */
        @SerialName("notification_seq") val notificationSeq: Long,
        /** 被设置/取消用户 QQ 号 */
        @SerialName("target_user_id") val targetUserId: Long,
        /** 是否被设置为管理员，`false` 表示被取消管理员 */
        @SerialName("is_set") val isSet: Boolean,
        /** 操作者（群主）QQ 号 */
        @SerialName("operator_id") val operatorId: Long,
    )

    /** 群成员被移除通知 */
    @Serializable
    @SerialName("kick")
    class Kick(
        /** 群号 */
        @SerialName("group_id") val groupId: Long,
        /** 通知序列号 */
        @SerialName("notification_seq") val notificationSeq: Long,
        /** 被移除用户 QQ 号 */
        @SerialName("target_user_id") val targetUserId: Long,
        /** 移除用户的管理员 QQ 号 */
        @SerialName("operator_id") val operatorId: Long,
    )

    /** 群成员退群通知 */
    @Serializable
    @SerialName("quit")
    class Quit(
        /** 群号 */
        @SerialName("group_id") val groupId: Long,
        /** 通知序列号 */
        @SerialName("notification_seq") val notificationSeq: Long,
        /** 退群用户 QQ 号 */
        @SerialName("target_user_id") val targetUserId: Long,
    )

    /** 群成员邀请他人入群请求 */
    @Serializable
    @SerialName("invited_join_request")
    class InvitedJoinRequest(
        /** 群号 */
        @SerialName("group_id") val groupId: Long,
        /** 通知序列号 */
        @SerialName("notification_seq") val notificationSeq: Long,
        /** 邀请者 QQ 号 */
        @SerialName("initiator_id") val initiatorId: Long,
        /** 被邀请用户 QQ 号 */
        @SerialName("target_user_id") val targetUserId: Long,
        /** 请求状态 */
        @SerialName("state") val state: String,
        /** 处理请求的管理员 QQ 号 */
        @SerialName("operator_id") val operatorId: Long? = null,
    )
}

/** 接收消息 */
@Serializable
@JsonClassDiscriminator("message_scene")
sealed class IncomingMessage {
    /** 好友消息 */
    @Serializable
    @SerialName("friend")
    class Friend(
        /** 好友 QQ 号或群号 */
        @SerialName("peer_id") val peerId: Long,
        /** 消息序列号 */
        @SerialName("message_seq") val messageSeq: Long,
        /** 发送者 QQ 号 */
        @SerialName("sender_id") val senderId: Long,
        /** 消息 Unix 时间戳（秒） */
        @SerialName("time") val time: Long,
        /** 消息段列表 */
        @SerialName("segments") val segments: List<IncomingSegment>,
        /** 好友信息 */
        @SerialName("friend") val friend: FriendEntity,
    )

    /** 群消息 */
    @Serializable
    @SerialName("group")
    class Group(
        /** 好友 QQ 号或群号 */
        @SerialName("peer_id") val peerId: Long,
        /** 消息序列号 */
        @SerialName("message_seq") val messageSeq: Long,
        /** 发送者 QQ 号 */
        @SerialName("sender_id") val senderId: Long,
        /** 消息 Unix 时间戳（秒） */
        @SerialName("time") val time: Long,
        /** 消息段列表 */
        @SerialName("segments") val segments: List<IncomingSegment>,
        /** 群信息 */
        @SerialName("group") val group: GroupEntity,
        /** 群成员信息 */
        @SerialName("group_member") val groupMember: GroupMemberEntity,
    )

    /** 临时会话消息 */
    @Serializable
    @SerialName("temp")
    class Temp(
        /** 好友 QQ 号或群号 */
        @SerialName("peer_id") val peerId: Long,
        /** 消息序列号 */
        @SerialName("message_seq") val messageSeq: Long,
        /** 发送者 QQ 号 */
        @SerialName("sender_id") val senderId: Long,
        /** 消息 Unix 时间戳（秒） */
        @SerialName("time") val time: Long,
        /** 消息段列表 */
        @SerialName("segments") val segments: List<IncomingSegment>,
        /** 临时会话发送者的所在的群信息 */
        @SerialName("group") val group: GroupEntity? = null,
    )
}

/** 接收转发消息 */
@Serializable
class IncomingForwardedMessage(
    /** 发送者名称 */
    @SerialName("sender_name") val senderName: String,
    /** 发送者头像 URL */
    @SerialName("avatar_url") val avatarUrl: String,
    /** 消息 Unix 时间戳（秒） */
    @SerialName("time") val time: Long,
    /** 消息段列表 */
    @SerialName("segments") val segments: List<IncomingSegment>,
)

/** 群精华消息 */
@Serializable
class GroupEssenceMessage(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 消息序列号 */
    @SerialName("message_seq") val messageSeq: Long,
    /** 消息发送时的 Unix 时间戳（秒） */
    @SerialName("message_time") val messageTime: Long,
    /** 发送者 QQ 号 */
    @SerialName("sender_id") val senderId: Long,
    /** 发送者名称 */
    @SerialName("sender_name") val senderName: String,
    /** 设置精华的操作者 QQ 号 */
    @SerialName("operator_id") val operatorId: Long,
    /** 设置精华的操作者名称 */
    @SerialName("operator_name") val operatorName: String,
    /** 消息被设置精华时的 Unix 时间戳（秒） */
    @SerialName("operation_time") val operationTime: Long,
    /** 消息段列表 */
    @SerialName("segments") val segments: List<IncomingSegment>,
)

/** 接收消息段 */
@Serializable
@JsonClassDiscriminator("type")
sealed class IncomingSegment {
    /** 文本消息段 */
    @Serializable
    @SerialName("text")
    class Text(
        val data: Data
    ) : IncomingSegment() {
        @Serializable
        class Data(
            /** 文本内容 */
            @SerialName("text") val text: String,
        )
    }

    /** 提及消息段 */
    @Serializable
    @SerialName("mention")
    class Mention(
        val data: Data
    ) : IncomingSegment() {
        @Serializable
        class Data(
            /** 提及的 QQ 号 */
            @SerialName("user_id") val userId: Long,
        )
    }

    /** 提及全体消息段 */
    @Serializable
    @SerialName("mention_all")
    class MentionAll(
        val data: Data
    ) : IncomingSegment() {
        @Serializable
        class Data(
        )
    }

    /** 表情消息段 */
    @Serializable
    @SerialName("face")
    class Face(
        val data: Data
    ) : IncomingSegment() {
        @Serializable
        class Data(
            /** 表情 ID */
            @SerialName("face_id") val faceId: String,
        )
    }

    /** 回复消息段 */
    @Serializable
    @SerialName("reply")
    class Reply(
        val data: Data
    ) : IncomingSegment() {
        @Serializable
        class Data(
            /** 被引用的消息序列号 */
            @SerialName("message_seq") val messageSeq: Long,
        )
    }

    /** 图片消息段 */
    @Serializable
    @SerialName("image")
    class Image(
        val data: Data
    ) : IncomingSegment() {
        @Serializable
        class Data(
            /** 资源 ID */
            @SerialName("resource_id") val resourceId: String,
            /** 临时 URL */
            @SerialName("temp_url") val tempUrl: String,
            /** 图片宽度 */
            @SerialName("width") val width: Int,
            /** 图片高度 */
            @SerialName("height") val height: Int,
            /** 图片预览文本 */
            @SerialName("summary") val summary: String,
            /** 图片类型 */
            @SerialName("sub_type") val subType: String,
        )
    }

    /** 语音消息段 */
    @Serializable
    @SerialName("record")
    class Record(
        val data: Data
    ) : IncomingSegment() {
        @Serializable
        class Data(
            /** 资源 ID */
            @SerialName("resource_id") val resourceId: String,
            /** 临时 URL */
            @SerialName("temp_url") val tempUrl: String,
            /** 语音时长（秒） */
            @SerialName("duration") val duration: Int,
        )
    }

    /** 视频消息段 */
    @Serializable
    @SerialName("video")
    class Video(
        val data: Data
    ) : IncomingSegment() {
        @Serializable
        class Data(
            /** 资源 ID */
            @SerialName("resource_id") val resourceId: String,
            /** 临时 URL */
            @SerialName("temp_url") val tempUrl: String,
            /** 视频宽度 */
            @SerialName("width") val width: Int,
            /** 视频高度 */
            @SerialName("height") val height: Int,
            /** 视频时长（秒） */
            @SerialName("duration") val duration: Int,
        )
    }

    /** 文件消息段 */
    @Serializable
    @SerialName("file")
    class File(
        val data: Data
    ) : IncomingSegment() {
        @Serializable
        class Data(
            /** 文件 ID */
            @SerialName("file_id") val fileId: String,
            /** 文件名称 */
            @SerialName("file_name") val fileName: String,
            /** 文件大小（字节） */
            @SerialName("file_size") val fileSize: Long,
            /** 文件的 TriSHA1 哈希值，仅在私聊文件中存在 */
            @SerialName("file_hash") val fileHash: String? = null,
        )
    }

    /** 合并转发消息段 */
    @Serializable
    @SerialName("forward")
    class Forward(
        val data: Data
    ) : IncomingSegment() {
        @Serializable
        class Data(
            /** 合并转发 ID */
            @SerialName("forward_id") val forwardId: String,
        )
    }

    /** 市场表情消息段 */
    @Serializable
    @SerialName("market_face")
    class MarketFace(
        val data: Data
    ) : IncomingSegment() {
        @Serializable
        class Data(
            /** 市场表情 URL */
            @SerialName("url") val url: String,
        )
    }

    /** 小程序消息段 */
    @Serializable
    @SerialName("light_app")
    class LightApp(
        val data: Data
    ) : IncomingSegment() {
        @Serializable
        class Data(
            /** 小程序名称 */
            @SerialName("app_name") val appName: String,
            /** 小程序 JSON 数据 */
            @SerialName("json_payload") val jsonPayload: String,
        )
    }

    /** XML 消息段 */
    @Serializable
    @SerialName("xml")
    class Xml(
        val data: Data
    ) : IncomingSegment() {
        @Serializable
        class Data(
            /** 服务 ID */
            @SerialName("service_id") val serviceId: Int,
            /** XML 数据 */
            @SerialName("xml_payload") val xmlPayload: String,
        )
    }
}

/** 发送转发消息 */
@Serializable
class OutgoingForwardedMessage(
    /** 发送者 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 发送者名称 */
    @SerialName("sender_name") val senderName: String,
    /** 消息段列表 */
    @SerialName("segments") val segments: List<OutgoingSegment>,
)

/** 发送消息段 */
@Serializable
@JsonClassDiscriminator("type")
sealed class OutgoingSegment {
    /** 文本消息段 */
    @Serializable
    @SerialName("text")
    class Text(
        val data: Data
    ) : OutgoingSegment() {
        @Serializable
        class Data(
            /** 文本内容 */
            @SerialName("text") val text: String,
        )
    }

    /** 提及消息段 */
    @Serializable
    @SerialName("mention")
    class Mention(
        val data: Data
    ) : OutgoingSegment() {
        @Serializable
        class Data(
            /** 提及的 QQ 号 */
            @SerialName("user_id") val userId: Long,
        )
    }

    /** 提及全体消息段 */
    @Serializable
    @SerialName("mention_all")
    class MentionAll(
        val data: Data
    ) : OutgoingSegment() {
        @Serializable
        class Data(
        )
    }

    /** 表情消息段 */
    @Serializable
    @SerialName("face")
    class Face(
        val data: Data
    ) : OutgoingSegment() {
        @Serializable
        class Data(
            /** 表情 ID */
            @SerialName("face_id") val faceId: String,
        )
    }

    /** 回复消息段 */
    @Serializable
    @SerialName("reply")
    class Reply(
        val data: Data
    ) : OutgoingSegment() {
        @Serializable
        class Data(
            /** 被引用的消息序列号 */
            @SerialName("message_seq") val messageSeq: Long,
        )
    }

    /** 图片消息段 */
    @Serializable
    @SerialName("image")
    class Image(
        val data: Data
    ) : OutgoingSegment() {
        @Serializable
        class Data(
            /** 文件 URI，支持 `file://` `http(s)://` `base64://` 三种格式 */
            @SerialName("uri") val uri: String,
            /** 图片预览文本 */
            @SerialName("summary") val summary: String? = null,
            /** 图片类型 */
            @SerialName("sub_type") val subType: String,
        )
    }

    /** 语音消息段 */
    @Serializable
    @SerialName("record")
    class Record(
        val data: Data
    ) : OutgoingSegment() {
        @Serializable
        class Data(
            /** 文件 URI，支持 `file://` `http(s)://` `base64://` 三种格式 */
            @SerialName("uri") val uri: String,
        )
    }

    /** 视频消息段 */
    @Serializable
    @SerialName("video")
    class Video(
        val data: Data
    ) : OutgoingSegment() {
        @Serializable
        class Data(
            /** 文件 URI，支持 `file://` `http(s)://` `base64://` 三种格式 */
            @SerialName("uri") val uri: String,
            /** 封面图片 URI */
            @SerialName("thumb_uri") val thumbUri: String? = null,
        )
    }

    /** 合并转发消息段 */
    @Serializable
    @SerialName("forward")
    class Forward(
        val data: Data
    ) : OutgoingSegment() {
        @Serializable
        class Data(
            /** 合并转发消息段 */
            @SerialName("messages") val messages: List<OutgoingForwardedMessage>,
        )
    }
}

// ####################################
// API Input and Output Structs
// ####################################

@Serializable
class ApiGeneralResponse(
    @SerialName("status") val status: String,
    @SerialName("retcode") val retcode: Int,
    @SerialName("data") val data: JsonElement? = null,
    @SerialName("message") val message: String? = null,
)

@Serializable
class ApiEmptyStruct

// ---- 系统 API ----

typealias GetLoginInfoInput = ApiEmptyStruct

@Serializable
class GetLoginInfoOutput(
    /** 登录 QQ 号 */
    @SerialName("uin") val uin: Long,
    /** 登录昵称 */
    @SerialName("nickname") val nickname: String,
)

typealias GetImplInfoInput = ApiEmptyStruct

@Serializable
class GetImplInfoOutput(
    /** 协议端名称 */
    @SerialName("impl_name") val implName: String,
    /** 协议端版本 */
    @SerialName("impl_version") val implVersion: String,
    /** 协议端使用的 QQ 协议版本 */
    @SerialName("qq_protocol_version") val qqProtocolVersion: String,
    /** 协议端使用的 QQ 协议平台 */
    @SerialName("qq_protocol_type") val qqProtocolType: String,
    /** 协议端实现的 Milky 协议版本，目前为 "1.0" */
    @SerialName("milky_version") val milkyVersion: String,
)

@Serializable
class GetUserProfileInput(
    /** 用户 QQ 号 */
    @SerialName("user_id") val userId: Long,
)

@Serializable
class GetUserProfileOutput(
    /** 昵称 */
    @SerialName("nickname") val nickname: String,
    /** QID */
    @SerialName("qid") val qid: String,
    /** 年龄 */
    @SerialName("age") val age: Int,
    /** 性别 */
    @SerialName("sex") val sex: String,
    /** 备注 */
    @SerialName("remark") val remark: String,
    /** 个性签名 */
    @SerialName("bio") val bio: String,
    /** QQ 等级 */
    @SerialName("level") val level: Int,
    /** 国家或地区 */
    @SerialName("country") val country: String,
    /** 城市 */
    @SerialName("city") val city: String,
    /** 学校 */
    @SerialName("school") val school: String,
)

@Serializable
class GetFriendListInput(
    /** 是否强制不使用缓存 */
    @SerialName("no_cache") val noCache: Boolean = false,
)

@Serializable
class GetFriendListOutput(
    /** 好友列表 */
    @SerialName("friends") val friends: List<FriendEntity>,
)

@Serializable
class GetFriendInfoInput(
    /** 好友 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 是否强制不使用缓存 */
    @SerialName("no_cache") val noCache: Boolean = false,
)

@Serializable
class GetFriendInfoOutput(
    /** 好友信息 */
    @SerialName("friend") val friend: FriendEntity,
)

@Serializable
class GetGroupListInput(
    /** 是否强制不使用缓存 */
    @SerialName("no_cache") val noCache: Boolean = false,
)

@Serializable
class GetGroupListOutput(
    /** 群列表 */
    @SerialName("groups") val groups: List<GroupEntity>,
)

@Serializable
class GetGroupInfoInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 是否强制不使用缓存 */
    @SerialName("no_cache") val noCache: Boolean = false,
)

@Serializable
class GetGroupInfoOutput(
    /** 群信息 */
    @SerialName("group") val group: GroupEntity,
)

@Serializable
class GetGroupMemberListInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 是否强制不使用缓存 */
    @SerialName("no_cache") val noCache: Boolean = false,
)

@Serializable
class GetGroupMemberListOutput(
    /** 群成员列表 */
    @SerialName("members") val members: List<GroupMemberEntity>,
)

@Serializable
class GetGroupMemberInfoInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 群成员 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 是否强制不使用缓存 */
    @SerialName("no_cache") val noCache: Boolean = false,
)

@Serializable
class GetGroupMemberInfoOutput(
    /** 群成员信息 */
    @SerialName("member") val member: GroupMemberEntity,
)

@Serializable
class GetCookiesInput(
    /** 需要获取 Cookies 的域名 */
    @SerialName("domain") val domain: String,
)

@Serializable
class GetCookiesOutput(
    /** 域名对应的 Cookies 字符串 */
    @SerialName("cookies") val cookies: String,
)

typealias GetCsrfTokenInput = ApiEmptyStruct

@Serializable
class GetCsrfTokenOutput(
    /** CSRF Token */
    @SerialName("csrf_token") val csrfToken: String,
)

// ---- 消息 API ----

@Serializable
class SendPrivateMessageInput(
    /** 好友 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 消息内容 */
    @SerialName("message") val message: List<OutgoingSegment>,
)

@Serializable
class SendPrivateMessageOutput(
    /** 消息序列号 */
    @SerialName("message_seq") val messageSeq: Long,
    /** 消息发送时间 */
    @SerialName("time") val time: Long,
)

@Serializable
class SendGroupMessageInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 消息内容 */
    @SerialName("message") val message: List<OutgoingSegment>,
)

@Serializable
class SendGroupMessageOutput(
    /** 消息序列号 */
    @SerialName("message_seq") val messageSeq: Long,
    /** 消息发送时间 */
    @SerialName("time") val time: Long,
)

@Serializable
class RecallPrivateMessageInput(
    /** 好友 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 消息序列号 */
    @SerialName("message_seq") val messageSeq: Long,
)

typealias RecallPrivateMessageOutput = ApiEmptyStruct

@Serializable
class RecallGroupMessageInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 消息序列号 */
    @SerialName("message_seq") val messageSeq: Long,
)

typealias RecallGroupMessageOutput = ApiEmptyStruct

@Serializable
class GetMessageInput(
    /** 消息场景 */
    @SerialName("message_scene") val messageScene: String,
    /** 好友 QQ 号或群号 */
    @SerialName("peer_id") val peerId: Long,
    /** 消息序列号 */
    @SerialName("message_seq") val messageSeq: Long,
)

@Serializable
class GetMessageOutput(
    /** 消息内容 */
    @SerialName("message") val message: IncomingMessage,
)

@Serializable
class GetHistoryMessagesInput(
    /** 消息场景 */
    @SerialName("message_scene") val messageScene: String,
    /** 好友 QQ 号或群号 */
    @SerialName("peer_id") val peerId: Long,
    /** 起始消息序列号，由此开始从新到旧查询，不提供则从最新消息开始 */
    @SerialName("start_message_seq") val startMessageSeq: Long? = null,
    /** 期望获取到的消息数量，最多 30 条 */
    @SerialName("limit") val limit: Int = 20,
)

@Serializable
class GetHistoryMessagesOutput(
    /** 获取到的消息（message_seq 升序排列），部分消息可能不存在，如撤回的消息 */
    @SerialName("messages") val messages: List<IncomingMessage>,
    /** 下一页起始消息序列号 */
    @SerialName("next_message_seq") val nextMessageSeq: Long? = null,
)

@Serializable
class GetResourceTempUrlInput(
    /** 资源 ID */
    @SerialName("resource_id") val resourceId: String,
)

@Serializable
class GetResourceTempUrlOutput(
    /** 临时资源链接 */
    @SerialName("url") val url: String,
)

@Serializable
class GetForwardedMessagesInput(
    /** 转发消息 ID */
    @SerialName("forward_id") val forwardId: String,
)

@Serializable
class GetForwardedMessagesOutput(
    /** 转发消息内容 */
    @SerialName("messages") val messages: List<IncomingForwardedMessage>,
)

@Serializable
class MarkMessageAsReadInput(
    /** 消息场景 */
    @SerialName("message_scene") val messageScene: String,
    /** 好友 QQ 号或群号 */
    @SerialName("peer_id") val peerId: Long,
    /** 标为已读的消息序列号，该消息及更早的消息将被标记为已读 */
    @SerialName("message_seq") val messageSeq: Long,
)

typealias MarkMessageAsReadOutput = ApiEmptyStruct

// ---- 好友 API ----

@Serializable
class SendFriendNudgeInput(
    /** 好友 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 是否戳自己 */
    @SerialName("is_self") val isSelf: Boolean = false,
)

typealias SendFriendNudgeOutput = ApiEmptyStruct

@Serializable
class SendProfileLikeInput(
    /** 好友 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 点赞数量 */
    @SerialName("count") val count: Int = 1,
)

typealias SendProfileLikeOutput = ApiEmptyStruct

@Serializable
class GetFriendRequestsInput(
    /** 获取的最大请求数量 */
    @SerialName("limit") val limit: Int = 20,
    /** `true` 表示只获取被过滤（由风险账号发起）的通知，`false` 表示只获取未被过滤的通知 */
    @SerialName("is_filtered") val isFiltered: Boolean = false,
)

@Serializable
class GetFriendRequestsOutput(
    /** 好友请求列表 */
    @SerialName("requests") val requests: List<FriendRequest>,
)

@Serializable
class AcceptFriendRequestInput(
    /** 请求发起者 UID */
    @SerialName("initiator_uid") val initiatorUid: String,
    /** 是否是被过滤的请求 */
    @SerialName("is_filtered") val isFiltered: Boolean = false,
)

typealias AcceptFriendRequestOutput = ApiEmptyStruct

@Serializable
class RejectFriendRequestInput(
    /** 请求发起者 UID */
    @SerialName("initiator_uid") val initiatorUid: String,
    /** 是否是被过滤的请求 */
    @SerialName("is_filtered") val isFiltered: Boolean = false,
    /** 拒绝理由 */
    @SerialName("reason") val reason: String? = null,
)

typealias RejectFriendRequestOutput = ApiEmptyStruct

// ---- 群聊 API ----

@Serializable
class SetGroupNameInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 新群名称 */
    @SerialName("new_group_name") val newGroupName: String,
)

typealias SetGroupNameOutput = ApiEmptyStruct

@Serializable
class SetGroupAvatarInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 头像文件 URI，支持 `file://` `http(s)://` `base64://` 三种格式 */
    @SerialName("image_uri") val imageUri: String,
)

typealias SetGroupAvatarOutput = ApiEmptyStruct

@Serializable
class SetGroupMemberCardInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 被设置的群成员 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 新群名片 */
    @SerialName("card") val card: String,
)

typealias SetGroupMemberCardOutput = ApiEmptyStruct

@Serializable
class SetGroupMemberSpecialTitleInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 被设置的群成员 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 新专属头衔 */
    @SerialName("special_title") val specialTitle: String,
)

typealias SetGroupMemberSpecialTitleOutput = ApiEmptyStruct

@Serializable
class SetGroupMemberAdminInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 被设置的 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 是否设置为管理员，`false` 表示取消管理员 */
    @SerialName("is_set") val isSet: Boolean = true,
)

typealias SetGroupMemberAdminOutput = ApiEmptyStruct

@Serializable
class SetGroupMemberMuteInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 被设置的 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 禁言持续时间（秒），设为 `0` 为取消禁言 */
    @SerialName("duration") val duration: Int = 0,
)

typealias SetGroupMemberMuteOutput = ApiEmptyStruct

@Serializable
class SetGroupWholeMuteInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 是否开启全员禁言，`false` 表示取消全员禁言 */
    @SerialName("is_mute") val isMute: Boolean = true,
)

typealias SetGroupWholeMuteOutput = ApiEmptyStruct

@Serializable
class KickGroupMemberInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 被踢的 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 是否拒绝加群申请，`false` 表示不拒绝 */
    @SerialName("reject_add_request") val rejectAddRequest: Boolean = false,
)

typealias KickGroupMemberOutput = ApiEmptyStruct

@Serializable
class GetGroupAnnouncementsInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
)

@Serializable
class GetGroupAnnouncementsOutput(
    /** 群公告列表 */
    @SerialName("announcements") val announcements: List<GroupAnnouncementEntity>,
)

@Serializable
class SendGroupAnnouncementInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 公告内容 */
    @SerialName("content") val content: String,
    /** 公告附带图像文件 URI，支持 `file://` `http(s)://` `base64://` 三种格式 */
    @SerialName("image_uri") val imageUri: String? = null,
)

typealias SendGroupAnnouncementOutput = ApiEmptyStruct

@Serializable
class DeleteGroupAnnouncementInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 公告 ID */
    @SerialName("announcement_id") val announcementId: String,
)

typealias DeleteGroupAnnouncementOutput = ApiEmptyStruct

@Serializable
class GetGroupEssenceMessagesInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 页码索引，从 0 开始 */
    @SerialName("page_index") val pageIndex: Int,
    /** 每页包含的精华消息数量 */
    @SerialName("page_size") val pageSize: Int,
)

@Serializable
class GetGroupEssenceMessagesOutput(
    /** 精华消息列表 */
    @SerialName("messages") val messages: List<GroupEssenceMessage>,
    /** 是否已到最后一页 */
    @SerialName("is_end") val isEnd: Boolean,
)

@Serializable
class SetGroupEssenceMessageInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 消息序列号 */
    @SerialName("message_seq") val messageSeq: Long,
    /** 是否设置为精华消息，`false` 表示取消精华 */
    @SerialName("is_set") val isSet: Boolean = true,
)

typealias SetGroupEssenceMessageOutput = ApiEmptyStruct

@Serializable
class QuitGroupInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
)

typealias QuitGroupOutput = ApiEmptyStruct

@Serializable
class SendGroupMessageReactionInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 要回应的消息序列号 */
    @SerialName("message_seq") val messageSeq: Long,
    /** 表情 ID */
    @SerialName("reaction") val reaction: String,
    /** 是否添加表情，`false` 表示取消 */
    @SerialName("is_add") val isAdd: Boolean = true,
)

typealias SendGroupMessageReactionOutput = ApiEmptyStruct

@Serializable
class SendGroupNudgeInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 被戳的群成员 QQ 号 */
    @SerialName("user_id") val userId: Long,
)

typealias SendGroupNudgeOutput = ApiEmptyStruct

@Serializable
class GetGroupNotificationsInput(
    /** 起始通知序列号 */
    @SerialName("start_notification_seq") val startNotificationSeq: Long? = null,
    /** `true` 表示只获取被过滤（由风险账号发起）的通知，`false` 表示只获取未被过滤的通知 */
    @SerialName("is_filtered") val isFiltered: Boolean = false,
    /** 获取的最大通知数量 */
    @SerialName("limit") val limit: Int = 20,
)

@Serializable
class GetGroupNotificationsOutput(
    /** 获取到的群通知（notification_seq 降序排列），序列号不一定连续 */
    @SerialName("notifications") val notifications: List<GroupNotification>,
    /** 下一页起始通知序列号 */
    @SerialName("next_notification_seq") val nextNotificationSeq: Long? = null,
)

@Serializable
class AcceptGroupRequestInput(
    /** 请求对应的通知序列号 */
    @SerialName("notification_seq") val notificationSeq: Long,
    /** 请求对应的通知类型 */
    @SerialName("notification_type") val notificationType: String,
    /** 请求所在的群号 */
    @SerialName("group_id") val groupId: Long,
    /** 是否是被过滤的请求 */
    @SerialName("is_filtered") val isFiltered: Boolean = false,
)

typealias AcceptGroupRequestOutput = ApiEmptyStruct

@Serializable
class RejectGroupRequestInput(
    /** 请求对应的通知序列号 */
    @SerialName("notification_seq") val notificationSeq: Long,
    /** 请求对应的通知类型 */
    @SerialName("notification_type") val notificationType: String,
    /** 请求所在的群号 */
    @SerialName("group_id") val groupId: Long,
    /** 是否是被过滤的请求 */
    @SerialName("is_filtered") val isFiltered: Boolean = false,
    /** 拒绝理由 */
    @SerialName("reason") val reason: String? = null,
)

typealias RejectGroupRequestOutput = ApiEmptyStruct

@Serializable
class AcceptGroupInvitationInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 邀请序列号 */
    @SerialName("invitation_seq") val invitationSeq: Long,
)

typealias AcceptGroupInvitationOutput = ApiEmptyStruct

@Serializable
class RejectGroupInvitationInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 邀请序列号 */
    @SerialName("invitation_seq") val invitationSeq: Long,
)

typealias RejectGroupInvitationOutput = ApiEmptyStruct

// ---- 文件 API ----

@Serializable
class UploadPrivateFileInput(
    /** 好友 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 文件 URI，支持 `file://` `http(s)://` `base64://` 三种格式 */
    @SerialName("file_uri") val fileUri: String,
    /** 文件名称 */
    @SerialName("file_name") val fileName: String,
)

@Serializable
class UploadPrivateFileOutput(
    /** 文件 ID */
    @SerialName("file_id") val fileId: String,
)

@Serializable
class UploadGroupFileInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 目标文件夹 ID */
    @SerialName("parent_folder_id") val parentFolderId: String = "/",
    /** 文件 URI，支持 `file://` `http(s)://` `base64://` 三种格式 */
    @SerialName("file_uri") val fileUri: String,
    /** 文件名称 */
    @SerialName("file_name") val fileName: String,
)

@Serializable
class UploadGroupFileOutput(
    /** 文件 ID */
    @SerialName("file_id") val fileId: String,
)

@Serializable
class GetPrivateFileDownloadUrlInput(
    /** 好友 QQ 号 */
    @SerialName("user_id") val userId: Long,
    /** 文件 ID */
    @SerialName("file_id") val fileId: String,
    /** 文件的 TriSHA1 哈希值 */
    @SerialName("file_hash") val fileHash: String,
)

@Serializable
class GetPrivateFileDownloadUrlOutput(
    /** 文件下载链接 */
    @SerialName("download_url") val downloadUrl: String,
)

@Serializable
class GetGroupFileDownloadUrlInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 文件 ID */
    @SerialName("file_id") val fileId: String,
)

@Serializable
class GetGroupFileDownloadUrlOutput(
    /** 文件下载链接 */
    @SerialName("download_url") val downloadUrl: String,
)

@Serializable
class GetGroupFilesInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 父文件夹 ID */
    @SerialName("parent_folder_id") val parentFolderId: String = "/",
)

@Serializable
class GetGroupFilesOutput(
    /** 文件列表 */
    @SerialName("files") val files: List<GroupFileEntity>,
    /** 文件夹列表 */
    @SerialName("folders") val folders: List<GroupFolderEntity>,
)

@Serializable
class MoveGroupFileInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 文件 ID */
    @SerialName("file_id") val fileId: String,
    /** 文件所在的文件夹 ID */
    @SerialName("parent_folder_id") val parentFolderId: String = "/",
    /** 目标文件夹 ID */
    @SerialName("target_folder_id") val targetFolderId: String = "/",
)

typealias MoveGroupFileOutput = ApiEmptyStruct

@Serializable
class RenameGroupFileInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 文件 ID */
    @SerialName("file_id") val fileId: String,
    /** 文件所在的文件夹 ID */
    @SerialName("parent_folder_id") val parentFolderId: String = "/",
    /** 新文件名称 */
    @SerialName("new_file_name") val newFileName: String,
)

typealias RenameGroupFileOutput = ApiEmptyStruct

@Serializable
class DeleteGroupFileInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 文件 ID */
    @SerialName("file_id") val fileId: String,
)

typealias DeleteGroupFileOutput = ApiEmptyStruct

@Serializable
class CreateGroupFolderInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 文件夹名称 */
    @SerialName("folder_name") val folderName: String,
)

@Serializable
class CreateGroupFolderOutput(
    /** 文件夹 ID */
    @SerialName("folder_id") val folderId: String,
)

@Serializable
class RenameGroupFolderInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 文件夹 ID */
    @SerialName("folder_id") val folderId: String,
    /** 新文件夹名 */
    @SerialName("new_folder_name") val newFolderName: String,
)

typealias RenameGroupFolderOutput = ApiEmptyStruct

@Serializable
class DeleteGroupFolderInput(
    /** 群号 */
    @SerialName("group_id") val groupId: Long,
    /** 文件夹 ID */
    @SerialName("folder_id") val folderId: String,
)

typealias DeleteGroupFolderOutput = ApiEmptyStruct

// ####################################
// API Endpoint Constants
// ####################################

sealed class ApiEndpoint<T : Any, R : Any>(val path: String) {
    /** 获取登录信息 */
    object GetLoginInfo : ApiEndpoint<GetLoginInfoInput, GetLoginInfoOutput>("/get_login_info")
    /** 获取协议端信息 */
    object GetImplInfo : ApiEndpoint<GetImplInfoInput, GetImplInfoOutput>("/get_impl_info")
    /** 获取用户个人信息 */
    object GetUserProfile : ApiEndpoint<GetUserProfileInput, GetUserProfileOutput>("/get_user_profile")
    /** 获取好友列表 */
    object GetFriendList : ApiEndpoint<GetFriendListInput, GetFriendListOutput>("/get_friend_list")
    /** 获取好友信息 */
    object GetFriendInfo : ApiEndpoint<GetFriendInfoInput, GetFriendInfoOutput>("/get_friend_info")
    /** 获取群列表 */
    object GetGroupList : ApiEndpoint<GetGroupListInput, GetGroupListOutput>("/get_group_list")
    /** 获取群信息 */
    object GetGroupInfo : ApiEndpoint<GetGroupInfoInput, GetGroupInfoOutput>("/get_group_info")
    /** 获取群成员列表 */
    object GetGroupMemberList : ApiEndpoint<GetGroupMemberListInput, GetGroupMemberListOutput>("/get_group_member_list")
    /** 获取群成员信息 */
    object GetGroupMemberInfo : ApiEndpoint<GetGroupMemberInfoInput, GetGroupMemberInfoOutput>("/get_group_member_info")
    /** 获取 Cookies */
    object GetCookies : ApiEndpoint<GetCookiesInput, GetCookiesOutput>("/get_cookies")
    /** 获取 CSRF Token */
    object GetCsrfToken : ApiEndpoint<GetCsrfTokenInput, GetCsrfTokenOutput>("/get_csrf_token")
    /** 发送私聊消息 */
    object SendPrivateMessage : ApiEndpoint<SendPrivateMessageInput, SendPrivateMessageOutput>("/send_private_message")
    /** 发送群聊消息 */
    object SendGroupMessage : ApiEndpoint<SendGroupMessageInput, SendGroupMessageOutput>("/send_group_message")
    /** 撤回私聊消息 */
    object RecallPrivateMessage : ApiEndpoint<RecallPrivateMessageInput, RecallPrivateMessageOutput>("/recall_private_message")
    /** 撤回群聊消息 */
    object RecallGroupMessage : ApiEndpoint<RecallGroupMessageInput, RecallGroupMessageOutput>("/recall_group_message")
    /** 获取消息 */
    object GetMessage : ApiEndpoint<GetMessageInput, GetMessageOutput>("/get_message")
    /** 获取历史消息列表 */
    object GetHistoryMessages : ApiEndpoint<GetHistoryMessagesInput, GetHistoryMessagesOutput>("/get_history_messages")
    /** 获取临时资源链接 */
    object GetResourceTempUrl : ApiEndpoint<GetResourceTempUrlInput, GetResourceTempUrlOutput>("/get_resource_temp_url")
    /** 获取合并转发消息内容 */
    object GetForwardedMessages : ApiEndpoint<GetForwardedMessagesInput, GetForwardedMessagesOutput>("/get_forwarded_messages")
    /** 标记消息为已读 */
    object MarkMessageAsRead : ApiEndpoint<MarkMessageAsReadInput, MarkMessageAsReadOutput>("/mark_message_as_read")
    /** 发送好友戳一戳 */
    object SendFriendNudge : ApiEndpoint<SendFriendNudgeInput, SendFriendNudgeOutput>("/send_friend_nudge")
    /** 发送名片点赞 */
    object SendProfileLike : ApiEndpoint<SendProfileLikeInput, SendProfileLikeOutput>("/send_profile_like")
    /** 获取好友请求列表 */
    object GetFriendRequests : ApiEndpoint<GetFriendRequestsInput, GetFriendRequestsOutput>("/get_friend_requests")
    /** 同意好友请求 */
    object AcceptFriendRequest : ApiEndpoint<AcceptFriendRequestInput, AcceptFriendRequestOutput>("/accept_friend_request")
    /** 拒绝好友请求 */
    object RejectFriendRequest : ApiEndpoint<RejectFriendRequestInput, RejectFriendRequestOutput>("/reject_friend_request")
    /** 设置群名称 */
    object SetGroupName : ApiEndpoint<SetGroupNameInput, SetGroupNameOutput>("/set_group_name")
    /** 设置群头像 */
    object SetGroupAvatar : ApiEndpoint<SetGroupAvatarInput, SetGroupAvatarOutput>("/set_group_avatar")
    /** 设置群名片 */
    object SetGroupMemberCard : ApiEndpoint<SetGroupMemberCardInput, SetGroupMemberCardOutput>("/set_group_member_card")
    /** 设置群成员专属头衔 */
    object SetGroupMemberSpecialTitle : ApiEndpoint<SetGroupMemberSpecialTitleInput, SetGroupMemberSpecialTitleOutput>("/set_group_member_special_title")
    /** 设置群管理员 */
    object SetGroupMemberAdmin : ApiEndpoint<SetGroupMemberAdminInput, SetGroupMemberAdminOutput>("/set_group_member_admin")
    /** 设置群成员禁言 */
    object SetGroupMemberMute : ApiEndpoint<SetGroupMemberMuteInput, SetGroupMemberMuteOutput>("/set_group_member_mute")
    /** 设置群全员禁言 */
    object SetGroupWholeMute : ApiEndpoint<SetGroupWholeMuteInput, SetGroupWholeMuteOutput>("/set_group_whole_mute")
    /** 踢出群成员 */
    object KickGroupMember : ApiEndpoint<KickGroupMemberInput, KickGroupMemberOutput>("/kick_group_member")
    /** 获取群公告列表 */
    object GetGroupAnnouncements : ApiEndpoint<GetGroupAnnouncementsInput, GetGroupAnnouncementsOutput>("/get_group_announcements")
    /** 发送群公告 */
    object SendGroupAnnouncement : ApiEndpoint<SendGroupAnnouncementInput, SendGroupAnnouncementOutput>("/send_group_announcement")
    /** 删除群公告 */
    object DeleteGroupAnnouncement : ApiEndpoint<DeleteGroupAnnouncementInput, DeleteGroupAnnouncementOutput>("/delete_group_announcement")
    /** 获取群精华消息列表 */
    object GetGroupEssenceMessages : ApiEndpoint<GetGroupEssenceMessagesInput, GetGroupEssenceMessagesOutput>("/get_group_essence_messages")
    /** 设置群精华消息 */
    object SetGroupEssenceMessage : ApiEndpoint<SetGroupEssenceMessageInput, SetGroupEssenceMessageOutput>("/set_group_essence_message")
    /** 退出群 */
    object QuitGroup : ApiEndpoint<QuitGroupInput, QuitGroupOutput>("/quit_group")
    /** 发送群消息表情回应 */
    object SendGroupMessageReaction : ApiEndpoint<SendGroupMessageReactionInput, SendGroupMessageReactionOutput>("/send_group_message_reaction")
    /** 发送群戳一戳 */
    object SendGroupNudge : ApiEndpoint<SendGroupNudgeInput, SendGroupNudgeOutput>("/send_group_nudge")
    /** 获取群通知列表 */
    object GetGroupNotifications : ApiEndpoint<GetGroupNotificationsInput, GetGroupNotificationsOutput>("/get_group_notifications")
    /** 同意入群/邀请他人入群请求 */
    object AcceptGroupRequest : ApiEndpoint<AcceptGroupRequestInput, AcceptGroupRequestOutput>("/accept_group_request")
    /** 拒绝入群/邀请他人入群请求 */
    object RejectGroupRequest : ApiEndpoint<RejectGroupRequestInput, RejectGroupRequestOutput>("/reject_group_request")
    /** 同意他人邀请自身入群 */
    object AcceptGroupInvitation : ApiEndpoint<AcceptGroupInvitationInput, AcceptGroupInvitationOutput>("/accept_group_invitation")
    /** 拒绝他人邀请自身入群 */
    object RejectGroupInvitation : ApiEndpoint<RejectGroupInvitationInput, RejectGroupInvitationOutput>("/reject_group_invitation")
    /** 上传私聊文件 */
    object UploadPrivateFile : ApiEndpoint<UploadPrivateFileInput, UploadPrivateFileOutput>("/upload_private_file")
    /** 上传群文件 */
    object UploadGroupFile : ApiEndpoint<UploadGroupFileInput, UploadGroupFileOutput>("/upload_group_file")
    /** 获取私聊文件下载链接 */
    object GetPrivateFileDownloadUrl : ApiEndpoint<GetPrivateFileDownloadUrlInput, GetPrivateFileDownloadUrlOutput>("/get_private_file_download_url")
    /** 获取群文件下载链接 */
    object GetGroupFileDownloadUrl : ApiEndpoint<GetGroupFileDownloadUrlInput, GetGroupFileDownloadUrlOutput>("/get_group_file_download_url")
    /** 获取群文件列表 */
    object GetGroupFiles : ApiEndpoint<GetGroupFilesInput, GetGroupFilesOutput>("/get_group_files")
    /** 移动群文件 */
    object MoveGroupFile : ApiEndpoint<MoveGroupFileInput, MoveGroupFileOutput>("/move_group_file")
    /** 重命名群文件 */
    object RenameGroupFile : ApiEndpoint<RenameGroupFileInput, RenameGroupFileOutput>("/rename_group_file")
    /** 删除群文件 */
    object DeleteGroupFile : ApiEndpoint<DeleteGroupFileInput, DeleteGroupFileOutput>("/delete_group_file")
    /** 创建群文件夹 */
    object CreateGroupFolder : ApiEndpoint<CreateGroupFolderInput, CreateGroupFolderOutput>("/create_group_folder")
    /** 重命名群文件夹 */
    object RenameGroupFolder : ApiEndpoint<RenameGroupFolderInput, RenameGroupFolderOutput>("/rename_group_folder")
    /** 删除群文件夹 */
    object DeleteGroupFolder : ApiEndpoint<DeleteGroupFolderInput, DeleteGroupFolderOutput>("/delete_group_folder")
}