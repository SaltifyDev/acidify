package org.ntqqrev.acidify.internal.service.system

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.SetFriendRequestReq
import org.ntqqrev.acidify.internal.service.NoOutputOidbService
import org.ntqqrev.acidify.pb.invoke

internal object SetNormalFriendRequest : NoOutputOidbService<SetNormalFriendRequest.Req>(0xb5d, 44) {
    class Req(
        val targetUid: String,
        val accept: Boolean
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        SetFriendRequestReq {
            it[accept] = if (payload.accept) 3 else 5
            it[targetUid] = payload.targetUid
        }.toByteArray()
}
