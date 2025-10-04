package org.ntqqrev.acidify.internal.service.file

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.PrivateFileDownloadReq
import org.ntqqrev.acidify.internal.packet.oidb.PrivateFileDownloadReqBody
import org.ntqqrev.acidify.internal.packet.oidb.PrivateFileDownloadResp
import org.ntqqrev.acidify.internal.service.OidbService
import org.ntqqrev.acidify.pb.invoke

internal object GetPrivateFileDownloadUrl : OidbService<GetPrivateFileDownloadUrl.Req, String>(0xe37, 1200, true) {
    class Req(
        val receiverUid: String,
        val fileUuid: String,
        val fileHash: String
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

    override fun parseOidb(client: LagrangeClient, payload: ByteArray): String {
        val resp = PrivateFileDownloadResp(payload).get { body }.get { result }
        val server = resp.get { server }
        val port = resp.get { port }
        val urlPath = resp.get { url }
        return "http://$server:$port$urlPath&isthumb=0"
    }
}