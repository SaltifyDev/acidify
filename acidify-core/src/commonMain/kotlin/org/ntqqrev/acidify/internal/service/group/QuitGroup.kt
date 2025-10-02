package org.ntqqrev.acidify.internal.service.group

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.QuitGroupReq
import org.ntqqrev.acidify.internal.service.NoOutputOidbService
import org.ntqqrev.acidify.pb.invoke

internal object QuitGroup : NoOutputOidbService<QuitGroup.Req>(0x1097, 1) {
    class Req(
        val groupUin: Long
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        QuitGroupReq {
            it[groupCode] = payload.groupUin
        }.toByteArray()
}

