package org.ntqqrev.acidify.internal.service.message

import korlibs.io.compression.deflate.GZIP
import korlibs.io.compression.uncompress
import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.message.CommonMessage
import org.ntqqrev.acidify.internal.packet.message.action.*
import org.ntqqrev.acidify.internal.service.Service
import org.ntqqrev.acidify.pb.PbObject
import org.ntqqrev.acidify.pb.invoke

internal object RecvLongMsg :
    Service<RecvLongMsg.Req, RecvLongMsg.Resp>("trpc.group.long_msg_interface.MsgService.SsoRecvLongMsg") {
    class Req(val resId: String, val isGroup: Boolean)

    class Resp(val messages: List<PbObject<CommonMessage>>)

    override fun build(client: LagrangeClient, payload: Req): ByteArray {
        return LongMsgInterfaceReq {
            it[recvReq] = LongMsgRecvReq {
                it[peerInfo] = LongMsgPeerInfo {
                    it[peerUid] = client.sessionStore.uid
                }
                it[resId] = payload.resId
                it[msgType] = if (payload.isGroup) 1 else 3
            }
            it[attr] = LongMsgAttr {
                it[subCmd] = 2
                it[clientType] = 1
                it[platform] = when (client.appInfo.os) {
                    "Windows" -> 3
                    "Linux" -> 6
                    "Mac" -> 7
                    else -> 0
                }
                it[proxyType] = 0
            }
        }.toByteArray()
    }

    override fun parse(client: LagrangeClient, payload: ByteArray): Resp {
        val resp = PbObject(LongMsgInterfaceResp, payload)
        val compressedPayload = resp.get { recvResp }?.get { this.payload }
            ?: throw IllegalStateException("No payload in LongMsgInterfaceResp")

        val decompressed = GZIP.uncompress(compressedPayload)
        val content = PbObject(PbMultiMsgTransmit, decompressed)

        val multiMsg = content.get { items }
            .firstOrNull { it.get { fileName } == "MultiMsg" }
            ?.get { buffer }?.get { msg }
            ?: emptyList()

        return Resp(multiMsg)
    }
}

