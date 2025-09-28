package org.ntqqrev.acidify.message

/**
 * 接收消息
 * @property scene 消息场景
 * @property peerUin 消息来源的 uin。
 * 对于[好友消息][MessageScene.FRIEND]，为好友的 QQ 号；
 * 对于[群聊消息][MessageScene.GROUP]，为群号。
 * @property peerUid 消息来源的 uid。
 * 对于[好友消息][MessageScene.FRIEND]，为好友的 uid；
 * 对于[群聊消息][MessageScene.GROUP]，为群号经过 [Long.toString] 的结果。
 * @property sequence 消息序列号
 * @property timestamp 消息发送的 Unix 事件戳（秒）
 * @property senderUin 发送者的 uin（QQ 号）
 * @property senderUid 发送者的 uid
 * @property senderName 发送者的名称，视情况有可能是昵称 / 备注 / 群名片等
 * @property segments 消息内容
 */
class BotIncomingMessage(
    val scene: MessageScene,
    val peerUin: Long,
    val peerUid: String,
    val sequence: Long,
    val timestamp: Long,
    val senderUin: Long,
    val senderUid: String,
    val senderName: String,
) {
    internal val segmentsMut = mutableListOf<BotIncomingSegment>()
    val segments: List<BotIncomingSegment>
        get() = segmentsMut
}