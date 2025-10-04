package org.ntqqrev.acidify.internal.service.system

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.FetchGroupNotificationsReq
import org.ntqqrev.acidify.internal.packet.oidb.FetchGroupNotificationsResp
import org.ntqqrev.acidify.internal.packet.oidb.GroupNotification
import org.ntqqrev.acidify.internal.service.OidbService
import org.ntqqrev.acidify.pb.PbObject
import org.ntqqrev.acidify.pb.invoke

internal abstract class FetchGroupNotifications(val isFiltered: Boolean) :
    OidbService<FetchGroupNotifications.Req, FetchGroupNotifications.Resp>(
        0x10c0,
        if (!isFiltered) 1 else 2
    ) {
    class Req(
        val startSequence: Long,
        val count: Int
    )

    class Resp(
        val nextSequence: Long,
        val notifications: List<PbObject<GroupNotification>>
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray =
        FetchGroupNotificationsReq {
            it[startSeq] = payload.startSequence
            it[count] = payload.count
        }.toByteArray()

    override fun parseOidb(client: LagrangeClient, payload: ByteArray): Resp {
        val resp = FetchGroupNotificationsResp(payload)
        return Resp(
            nextSequence = resp.get { nextStartSeq },
            notifications = resp.get { notifications }
        )
    }

    object Normal : FetchGroupNotifications(false)

    object Filtered : FetchGroupNotifications(true)
}