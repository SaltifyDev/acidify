package org.ntqqrev.acidify.internal.service.file

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.PrivateFileDownloadReq
import org.ntqqrev.acidify.internal.packet.oidb.PrivateFileDownloadReqBody
import org.ntqqrev.acidify.internal.packet.oidb.PrivateFileDownloadResp
import org.ntqqrev.acidify.internal.service.OidbService
import org.ntqqrev.acidify.pb.invoke

internal object GetPrivateFileDownloadUrl :
    OidbService<GetPrivateFileDownloadUrl.Req, GetPrivateFileDownloadUrl.Resp>(0xe37, 1200, true) {
    class Req(
        val receiverUid: String,
        val fileUuid: String,
        val fileHash: String
    )

    class Resp(
        val url: String
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        PrivateFileDownloadReq {
            it[body] = PrivateFileDownloadReqBody {
                it[receiverUid] = payload.receiverUid
                it[fileUuid] = payload.fileUuid
                it[fileHash] = payload.fileHash
                it[t2] = 0
            }
        }.toByteArray()

    override fun parseOidb(client: LagrangeClient, payload: ByteArray): Resp {
        val resp = PrivateFileDownloadResp(payload).get { body }.get { result }
        val server = resp.get { server }
        val port = resp.get { port }
        val urlPath = resp.get { url }
        return Resp("http://$server:$port$urlPath&isthumb=0")
    }
}