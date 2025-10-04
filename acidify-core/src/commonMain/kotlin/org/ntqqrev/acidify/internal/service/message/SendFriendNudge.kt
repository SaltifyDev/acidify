package org.ntqqrev.acidify.internal.service.message

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.PokeReq
import org.ntqqrev.acidify.internal.service.NoOutputOidbService
import org.ntqqrev.acidify.pb.invoke

internal object SendFriendNudge : NoOutputOidbService<SendFriendNudge.Req>(0xed3, 1) {
    class Req(
        val friendUin: Long,
        val isSelf: Boolean = false
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        PokeReq {
            it[targetUin] = if (payload.isSelf) client.sessionStore.uin else payload.friendUin
            it[groupUin] = 0
            it[friendUin] = payload.friendUin
            it[ext] = 0
        }.toByteArray()
}