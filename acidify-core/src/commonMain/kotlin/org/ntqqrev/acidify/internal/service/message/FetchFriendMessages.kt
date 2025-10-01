package org.ntqqrev.acidify.internal.service.message

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.message.CommonMessage
import org.ntqqrev.acidify.internal.packet.message.action.SsoGetC2cMsgReq
import org.ntqqrev.acidify.internal.packet.message.action.SsoGetC2cMsgResp
import org.ntqqrev.acidify.internal.service.Service
import org.ntqqrev.acidify.pb.PbObject
import org.ntqqrev.acidify.pb.invoke

internal object FetchFriendMessages :
    Service<FetchFriendMessages.Req, List<PbObject<CommonMessage>>>("trpc.msg.register_proxy.RegisterProxy.SsoGetC2cMsg") {
    class Req(
        val peerUid: String,
        val startSequence: Long,
        val endSequence: Long
    )

    override fun build(client: LagrangeClient, payload: Req): ByteArray {
        return SsoGetC2cMsgReq {
            it[peerUid] = payload.peerUid
            it[startSequence] = payload.startSequence
            it[endSequence] = payload.endSequence
        }.toByteArray()
    }

    override fun parse(client: LagrangeClient, payload: ByteArray): List<PbObject<CommonMessage>> {
        val resp = SsoGetC2cMsgResp(payload)
        val retcode = resp.get { retcode }
        val errorMsg = resp.get { errorMsg }

        if (retcode != 0) {
            throw Exception("Failed to get friend messages: $errorMsg (retcode=$retcode)")
        }

        return resp.get { messages }
    }
}

