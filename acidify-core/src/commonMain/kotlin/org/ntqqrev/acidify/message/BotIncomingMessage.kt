package org.ntqqrev.acidify.message

import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.internal.packet.message.CommonMessage
import org.ntqqrev.acidify.internal.packet.message.PushMsgType
import org.ntqqrev.acidify.message.internal.IncomingSegmentFactory
import org.ntqqrev.acidify.message.internal.MessageParsingContext
import org.ntqqrev.acidify.pb.PbObject

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

    companion object {
        internal val factories = listOf<IncomingSegmentFactory<*>>(
            IncomingSegmentFactory.Text,
        )

        internal fun Bot.parseMessage(raw: PbObject<CommonMessage>): BotIncomingMessage? {
            val routingHead = raw.get { routingHead }
            val contentHead = raw.get { contentHead }
            val draftMsg = when (PushMsgType.from(contentHead.get { type })) {
                PushMsgType.FriendMessage, PushMsgType.FriendRecordMessage -> {
                    val isSelfSend = routingHead.get { fromUin } == this.uin
                    BotIncomingMessage(
                        scene = MessageScene.FRIEND,
                        peerUin = if (isSelfSend) routingHead.get { toUin } else routingHead.get { fromUin },
                        peerUid = if (isSelfSend) routingHead.get { toUid } else routingHead.get { fromUid },
                        sequence = contentHead.get { clientSequence },
                        timestamp = contentHead.get { time },
                        senderUin = routingHead.get { fromUin },
                        senderUid = routingHead.get { fromUid },
                        senderName = routingHead.get { commonC2C }.get { name },
                    )
                }
                PushMsgType.GroupMessage -> {
                    BotIncomingMessage(
                        scene = MessageScene.GROUP,
                        peerUin = routingHead.get { group }.get { groupCode },
                        peerUid = routingHead.get { toUid },
                        sequence = contentHead.get { clientSequence },
                        timestamp = contentHead.get { time },
                        senderUin = routingHead.get { fromUin },
                        senderUid = routingHead.get { fromUid },
                        senderName = routingHead.get { group }.get { groupCard },
                    )
                }
                else -> return null
            }
            val elems = raw.get { messageBody }.get { richText }.get { elems }
            val ctx = MessageParsingContext(draftMsg, elems, this)
            while (ctx.hasNext()) {
                var matched = false
                for (factory in factories) {
                    val segment = factory.tryParse(ctx) ?: continue
                    draftMsg.segmentsMut += segment
                    matched = true
                    break
                }
                if (!matched) {
                    ctx.skip()
                }
            }
            return draftMsg.takeIf { it.segments.isNotEmpty() }
        }
    }
}