package org.ntqqrev.acidify.event

/**
 * 好友请求事件
 * @property initiatorUin 申请好友的用户 QQ 号
 * @property initiatorUid 用户 uid
 * @property comment 申请附加信息
 * @property via 申请来源
 */
class FriendRequestEvent(
    val initiatorUin: Long,
    val initiatorUid: String,
    val comment: String,
    val via: String
) : AcidifyEvent
