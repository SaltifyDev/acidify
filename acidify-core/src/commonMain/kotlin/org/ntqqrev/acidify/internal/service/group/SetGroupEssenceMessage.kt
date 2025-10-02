package org.ntqqrev.acidify.internal.service.group

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.SetGroupEssenceMessageReq
import org.ntqqrev.acidify.internal.service.NoOutputOidbService
import org.ntqqrev.acidify.pb.invoke

internal object SetGroupEssenceMessage : NoOutputOidbService<SetGroupEssenceMessage.Req>(0xeac, 1) {
    class Req(
        val groupUin: Long,
        val sequence: Int,
        val random: Int
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        SetGroupEssenceMessageReq {
            it[groupCode] = payload.groupUin
            it[sequence] = payload.sequence
            it[random] = payload.random
        }.toByteArray()
}

