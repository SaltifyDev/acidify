package org.ntqqrev.acidify.struct

/**
 * 群通知实体
 */
sealed class BotGroupNotification {
    /**
     * 用户入群请求
     * @property groupUin 群号
     * @property notificationSeq 通知序列号
     * @property isFiltered 请求是否被过滤（发起自风险账户）
     * @property initiatorUin 发起者 QQ 号
     * @property initiatorUid 发起者 uid
     * @property state 请求状态
     * @property operatorUin 处理请求的管理员 QQ 号
     * @property operatorUid 处理请求的管理员 uid
     * @property comment 入群请求附加信息
     */
    data class JoinRequest(
        val groupUin: Long,
        val notificationSeq: Long,
        val isFiltered: Boolean,
        val initiatorUin: Long,
        val initiatorUid: String,
        val state: String,
        val operatorUin: Long?,
        val operatorUid: String?,
        val comment: String
    ) : BotGroupNotification()

    /**
     * 群管理员变更通知
     * @property groupUin 群号
     * @property notificationSeq 通知序列号
     * @property targetUserUin 被设置/取消用户 QQ 号
     * @property targetUserUid 被设置/取消用户 uid
     * @property isSet 是否被设置为管理员，`false` 表示被取消管理员
     * @property operatorUin 操作者（群主）QQ 号
     * @property operatorUid 操作者（群主）uid
     */
    data class AdminChange(
        val groupUin: Long,
        val notificationSeq: Long,
        val targetUserUin: Long,
        val targetUserUid: String,
        val isSet: Boolean,
        val operatorUin: Long,
        val operatorUid: String
    ) : BotGroupNotification()

    /**
     * 群成员被移除通知
     * @property groupUin 群号
     * @property notificationSeq 通知序列号
     * @property targetUserUin 被移除用户 QQ 号
     * @property targetUserUid 被移除用户 uid
     * @property operatorUin 移除用户的管理员 QQ 号
     * @property operatorUid 移除用户的管理员 uid
     */
    data class Kick(
        val groupUin: Long,
        val notificationSeq: Long,
        val targetUserUin: Long,
        val targetUserUid: String,
        val operatorUin: Long,
        val operatorUid: String
    ) : BotGroupNotification()

    /**
     * 群成员退群通知
     * @property groupUin 群号
     * @property notificationSeq 通知序列号
     * @property targetUserUin 退群用户 QQ 号
     * @property targetUserUid 退群用户 uid
     */
    data class Quit(
        val groupUin: Long,
        val notificationSeq: Long,
        val targetUserUin: Long,
        val targetUserUid: String
    ) : BotGroupNotification()

    /**
     * 群成员邀请他人入群请求
     * @property groupUin 群号
     * @property notificationSeq 通知序列号
     * @property initiatorUin 邀请者 QQ 号
     * @property initiatorUid 邀请者 uid
     * @property targetUserUin 被邀请用户 QQ 号
     * @property targetUserUid 被邀请用户 uid
     * @property state 请求状态
     * @property operatorUin 处理请求的管理员 QQ 号
     * @property operatorUid 处理请求的管理员 uid
     */
    data class InvitedJoinRequest(
        val groupUin: Long,
        val notificationSeq: Long,
        val initiatorUin: Long,
        val initiatorUid: String,
        val targetUserUin: Long,
        val targetUserUid: String,
        val state: String,
        val operatorUin: Long?,
        val operatorUid: String?
    ) : BotGroupNotification()
}