package org.ntqqrev.acidify.internal.service.group

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.SetMemberMuteReq
import org.ntqqrev.acidify.internal.service.NoOutputOidbService
import org.ntqqrev.acidify.pb.invoke

internal object SetMemberMute : NoOutputOidbService<SetMemberMute.Req>(0x1253, 1) {
    class Req(
        val groupUin: Long,
        val memberUid: String,
        val duration: Int
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        SetMemberMuteReq {
            it[groupCode] = payload.groupUin
            it[type] = 1
            it[targetUid] = payload.memberUid
            it[duration] = payload.duration
        }.toByteArray()
}

