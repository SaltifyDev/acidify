package org.ntqqrev.acidify.internal.service.group

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.SetGroupWholeMuteReq
import org.ntqqrev.acidify.internal.service.NoOutputOidbService
import org.ntqqrev.acidify.pb.invoke

internal object SetGroupWholeMute : NoOutputOidbService<SetGroupWholeMute.Req>(0x89a, 0) {
    class Req(
        val groupUin: Long,
        val isMute: Boolean
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        SetGroupWholeMuteReq {
            it[groupCode] = payload.groupUin
            it[state] = SetGroupWholeMuteReq.State {
                it[isMute] = if (payload.isMute) -1 else 0
            }
        }.toByteArray()
}

