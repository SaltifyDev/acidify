package org.ntqqrev.acidify.internal.service.system

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.ProfileLikeReq
import org.ntqqrev.acidify.internal.service.NoOutputOidbService
import org.ntqqrev.acidify.pb.invoke

internal object SendProfileLike : NoOutputOidbService<SendProfileLike.Req>(0x7e5, 104) {
    class Req(
        val targetUid: String,
        val count: Int
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        ProfileLikeReq {
            it[targetUid] = payload.targetUid
            it[field2] = 71
            it[field3] = payload.count
        }.toByteArray()
}