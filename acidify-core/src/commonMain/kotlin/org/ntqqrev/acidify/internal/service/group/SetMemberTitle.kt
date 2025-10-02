package org.ntqqrev.acidify.internal.service.group

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.Oidb0x8FCReq
import org.ntqqrev.acidify.internal.service.NoOutputOidbService
import org.ntqqrev.acidify.pb.invoke

internal object SetMemberTitle : NoOutputOidbService<SetMemberTitle.Req>(0x8fc, 2) {
    class Req(
        val groupUin: Long,
        val memberUid: String,
        val specialTitle: String
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        Oidb0x8FCReq {
            it[groupCode] = payload.groupUin
            it[memLevelInfo] = listOf(
                Oidb0x8FCReq.MemberInfo {
                    it[uid] = payload.memberUid
                    it[specialTitle] = payload.specialTitle.encodeToByteArray()
                }
            )
        }.toByteArray()
}