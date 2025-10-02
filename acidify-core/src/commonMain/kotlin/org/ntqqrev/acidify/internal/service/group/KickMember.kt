package org.ntqqrev.acidify.internal.service.group

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.KickMemberReq
import org.ntqqrev.acidify.internal.service.NoOutputOidbService
import org.ntqqrev.acidify.pb.invoke

internal object KickMember : NoOutputOidbService<KickMember.Req>(0x8a0, 1) {
    class Req(
        val groupUin: Long,
        val memberUid: String,
        val rejectAddRequest: Boolean,
        val reason: String
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        KickMemberReq {
            it[groupCode] = payload.groupUin
            it[kickFlag] = 0
            it[targetUid] = payload.memberUid
            it[rejectAddRequest] = payload.rejectAddRequest
            it[reason] = payload.reason
        }.toByteArray()
}

