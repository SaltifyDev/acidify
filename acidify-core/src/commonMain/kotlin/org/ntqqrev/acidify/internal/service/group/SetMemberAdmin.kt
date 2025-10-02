package org.ntqqrev.acidify.internal.service.group

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.SetMemberAdminReq
import org.ntqqrev.acidify.internal.service.NoOutputOidbService
import org.ntqqrev.acidify.pb.invoke

internal object SetMemberAdmin : NoOutputOidbService<SetMemberAdmin.Req>(0x1096, 1) {
    class Req(
        val groupUin: Long,
        val memberUid: String,
        val isAdmin: Boolean
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        SetMemberAdminReq {
            it[groupCode] = payload.groupUin
            it[targetUid] = payload.memberUid
            it[isAdmin] = payload.isAdmin
        }.toByteArray()
}

