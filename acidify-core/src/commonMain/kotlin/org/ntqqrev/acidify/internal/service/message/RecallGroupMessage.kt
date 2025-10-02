package org.ntqqrev.acidify.internal.service.message

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.message.action.GroupRecallMsg
import org.ntqqrev.acidify.internal.service.NoOutputService
import org.ntqqrev.acidify.pb.invoke

internal object RecallGroupMessage :
    NoOutputService<RecallGroupMessage.Req>("trpc.msg.msg_svc.MsgService.SsoGroupRecallMsg") {
    class Req(
        val groupUin: Long,
        val sequence: Long
    )

    override fun build(client: LagrangeClient, payload: Req): ByteArray {
        return GroupRecallMsg {
            it[type] = 1
            it[groupUin] = payload.groupUin
            it[info] = GroupRecallMsg.Info {
                it[sequence] = payload.sequence
                it[field3] = 0
            }
            it[field4] = GroupRecallMsg.Field4 {
                it[field1] = 0
            }
        }.toByteArray()
    }
}