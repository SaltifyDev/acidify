package org.ntqqrev.acidify.internal.service.system

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.system.RegisterInfoResponse
import org.ntqqrev.acidify.internal.packet.system.UnRegisterInfo
import org.ntqqrev.acidify.internal.service.NoInputService
import org.ntqqrev.acidify.internal.util.generateDeviceInfo
import org.ntqqrev.acidify.pb.invoke

internal object BotOffline : NoInputService<String>("trpc.qq_new_tech.status_svc.StatusService.UnRegister") {
    override fun build(client: LagrangeClient, payload: Unit): ByteArray = UnRegisterInfo {
        it[device] = client.generateDeviceInfo()
    }.toByteArray()

    override fun parse(client: LagrangeClient, payload: ByteArray): String =
        RegisterInfoResponse(payload).get { message }
}