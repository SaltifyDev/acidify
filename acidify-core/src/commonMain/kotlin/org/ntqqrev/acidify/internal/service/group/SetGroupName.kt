package org.ntqqrev.acidify.internal.service.group

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.SetGroupNameReq
import org.ntqqrev.acidify.internal.service.NoOutputOidbService
import org.ntqqrev.acidify.pb.invoke

internal object SetGroupName : NoOutputOidbService<SetGroupName.Req>(0x89a, 15) {
    class Req(
        val groupUin: Long,
        val groupName: String
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        SetGroupNameReq {
            it[groupCode] = payload.groupUin
            it[targetName] = payload.groupName
        }.toByteArray()
}

