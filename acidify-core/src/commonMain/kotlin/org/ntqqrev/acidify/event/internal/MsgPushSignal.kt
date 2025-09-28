package org.ntqqrev.acidify.event.internal

import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.event.AcidifyEvent
import org.ntqqrev.acidify.event.MessageReceiveEvent
import org.ntqqrev.acidify.internal.packet.message.PushMsg
import org.ntqqrev.acidify.internal.packet.message.PushMsgType
import org.ntqqrev.acidify.message.BotIncomingMessage.Companion.parseMessage
import org.ntqqrev.acidify.pb.invoke

internal object MsgPushSignal : Signal("trpc.msg.olpush.OlPushService.MsgPush") {
    override fun parse(bot: Bot, payload: ByteArray): List<AcidifyEvent> {
        val commonMsg = PushMsg(payload).get { message }
        return when (PushMsgType.from(commonMsg.get { contentHead }.get { type })) {
            PushMsgType.FriendMessage, PushMsgType.FriendRecordMessage, PushMsgType.GroupMessage -> {
                val msg = bot.parseMessage(commonMsg) ?: return listOf()
                // todo: resolve group invite card
                listOf(MessageReceiveEvent(msg))
            }

            else -> listOf()
        }
    }
}