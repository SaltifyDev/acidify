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

internal object SendGroupMessage : Service<SendGroupMessage.Req, SendGroupMessage.Resp>("MessageSvc.PbSendMsg") {
    class Req(
        val groupUin: Long,
        val elems: List<PbObject<Elem>>,
        val clientSequence: Long,
        val random: Int,
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
                it[group] = SendRoutingHead.Grp {
                    it[groupUin] = payload.groupUin
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
            it[clientSequence] = payload.clientSequence
            it[random] = payload.random
        }.toByteArray()
    }

    override fun parse(client: LagrangeClient, payload: ByteArray): Resp {
        val resp = PbSendMsgResp(payload)
        return Resp(
            result = resp.get { result },
            errMsg = resp.get { errMsg },
            sendTime = resp.get { sendTime },
            sequence = resp.get { sequence }
        )
    }
}