package org.ntqqrev.acidify.internal.service.system

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.system.SsoHeartBeat
import org.ntqqrev.acidify.internal.service.NoInputService
import org.ntqqrev.acidify.pb.invoke

internal object Heartbeat : NoInputService<Unit>("trpc.qq_new_tech.status_svc.StatusService.SsoHeartBeat") {
    override fun build(client: LagrangeClient, payload: Unit): ByteArray = SsoHeartBeat {
        it[type] = 1
    }.toByteArray()

    override fun parse(client: LagrangeClient, payload: ByteArray) = Unit
}