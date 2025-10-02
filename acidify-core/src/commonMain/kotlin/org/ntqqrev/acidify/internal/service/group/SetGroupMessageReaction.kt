package org.ntqqrev.acidify.internal.service.group

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.SetGroupMessageReactionReq
import org.ntqqrev.acidify.internal.service.NoOutputOidbService
import org.ntqqrev.acidify.pb.invoke

internal object SetGroupMessageReaction : NoOutputOidbService<SetGroupMessageReaction.Req>(0x9082, 1) {
    class Req(
        val groupUin: Long,
        val sequence: Int,
        val code: String,
        val isAdd: Boolean
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        SetGroupMessageReactionReq {
            it[groupCode] = payload.groupUin
            it[sequence] = payload.sequence
            it[code] = payload.code
            it[type] = if (payload.isAdd) 1 else 2
            it[field6] = false
            it[field7] = false
        }.toByteArray()
}

