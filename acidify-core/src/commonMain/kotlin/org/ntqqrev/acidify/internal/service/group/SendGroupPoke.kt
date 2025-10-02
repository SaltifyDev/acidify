package org.ntqqrev.acidify.internal.service.group

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.SendGroupPokeReq
import org.ntqqrev.acidify.internal.service.NoOutputOidbService
import org.ntqqrev.acidify.pb.invoke

internal object SendGroupPoke : NoOutputOidbService<SendGroupPoke.Req>(0xed3, 1) {
    class Req(
        val groupUin: Long,
        val targetUin: Long
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        SendGroupPokeReq {
            it[uin] = payload.targetUin
            it[groupCode] = payload.groupUin
            it[friendUin] = 0L
            it[ext] = 0
        }.toByteArray()
}

