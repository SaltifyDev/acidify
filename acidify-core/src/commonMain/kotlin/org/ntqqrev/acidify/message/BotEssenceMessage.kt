package org.ntqqrev.acidify.message

/**
 * 群精华消息
 * @property groupUin 群号
 * @property messageSeq 消息序列号
 * @property messageTime 消息发送时的 Unix 时间戳（秒）
 * @property senderUin 发送者 QQ 号
 * @property senderName 发送者名称
 * @property operatorUin 设置精华的操作者 QQ 号
 * @property operatorName 设置精华的操作者名称
 * @property operationTime 消息被设置精华时的 Unix 时间戳（秒）
 * @property segments 消息段列表
 */
class BotEssenceMessage(
    val groupUin: Long,
    val messageSeq: Long,
    val messageTime: Long,
    val senderUin: Long,
    val senderName: String,
    val operatorUin: Long,
    val operatorName: String,
    val operationTime: Long,
    val segments: List<BotEssenceSegment>
)