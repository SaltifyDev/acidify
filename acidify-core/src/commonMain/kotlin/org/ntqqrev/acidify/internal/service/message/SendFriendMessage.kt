package org.ntqqrev.acidify.internal.service.message

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.message.Elem
import org.ntqqrev.acidify.internal.packet.message.MessageBody
import org.ntqqrev.acidify.internal.packet.message.RichText
import org.ntqqrev.acidify.internal.packet.message.SendContentHead
import org.ntqqrev.acidify.internal.packet.message.SendRoutingHead
import org.ntqqrev.acidify.internal.packet.message.action.PbSendMsgReq
import org.ntqqrev.acidify.internal.packet.message.action.PbSendMsgResp
import org.ntqqrev.acidify.internal.service.Service
import org.ntqqrev.acidify.pb.PbObject
import org.ntqqrev.acidify.pb.invoke
import kotlin.random.Random

internal object SendFriendMessage : Service<SendFriendMessage.Req, SendFriendMessage.Resp>("MessageSvc.PbSendMsg") {
    class Req(
        val friendUin: Long,
        val friendUid: String,
        val elems: List<PbObject<Elem>>,
    )

    class Resp(
        val result: Int,
        val errMsg: String,
        val sendTime: Long,
        val sequence: Long
    )

    override fun build(client: LagrangeClient, payload: Req): ByteArray {
        return PbSendMsgReq {
            it[routingHead] = SendRoutingHead {
                it[c2c] = SendRoutingHead.C2C {
                    it[peerUin] = payload.friendUin
                    it[peerUid] = payload.friendUid
                }
            }
            it[contentHead] = SendContentHead {
                it[pkgNum] = 1
            }
            it[messageBody] = MessageBody {
                it[richText] = RichText {
                    it[elems] = payload.elems
                }
            }
            it[clientSequence] = Random.nextLong()
            it[random] = Random.nextInt()
        }.toByteArray()
    }

    override fun parse(client: LagrangeClient, payload: ByteArray): Resp {
        val resp = PbSendMsgResp(payload)
        return Resp(
            result = resp.get { result },
            errMsg = resp.get { errMsg },
            sendTime = resp.get { sendTime },
            sequence = resp.get { clientSequence }
        )
    }
}

