package org.ntqqrev.acidify.internal.service.file

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.Oidb0x6D7Req
import org.ntqqrev.acidify.internal.packet.oidb.Oidb0x6D7Resp
import org.ntqqrev.acidify.internal.service.OidbService
import org.ntqqrev.acidify.pb.invoke

internal object CreateGroupFolder : OidbService<CreateGroupFolder.Req, CreateGroupFolder.Resp>(0x6d7, 0, true) {
    class Req(
        val groupUin: Long,
        val folderName: String
    )

    class Resp(
        val folderId: String
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        Oidb0x6D7Req {
            it[createFolder] = Oidb0x6D7Req.CreateFolder {
                it[groupUin] = payload.groupUin
                it[rootDirectory] = "/"
                it[folderName] = payload.folderName
            }
        }.toByteArray()

    override fun parseOidb(client: LagrangeClient, payload: ByteArray): Resp {
        val resp = Oidb0x6D7Resp(payload).get { createFolder }
        val retCode = resp.get { retCode }
        if (retCode != 0) {
            val retMsg = resp.get { retMsg }
            throw Exception("创建群文件夹失败: $retCode $retMsg")
        }
        return Resp(folderId = resp.get { folderId })
    }
}
