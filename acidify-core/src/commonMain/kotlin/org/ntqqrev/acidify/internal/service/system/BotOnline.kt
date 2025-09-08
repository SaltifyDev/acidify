package org.ntqqrev.acidify.internal.service.system

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.system.OnlineBusinessInfo
import org.ntqqrev.acidify.internal.packet.system.RegisterInfo
import org.ntqqrev.acidify.internal.packet.system.RegisterInfoResponse
import org.ntqqrev.acidify.internal.service.NoInputService
import org.ntqqrev.acidify.internal.util.generateDeviceInfo
import org.ntqqrev.acidify.internal.util.toHex
import org.ntqqrev.acidify.pb.invoke

internal object BotOnline : NoInputService<String>("trpc.qq_new_tech.status_svc.StatusService.Register") {
    override fun build(client: LagrangeClient, payload: Unit): ByteArray = RegisterInfo {
        it[guid] = client.sessionStore.guid.toHex()
        it[currentVersion] = client.appInfo.currentVersion
        it[device] = client.generateDeviceInfo()
        it[businessInfo] = OnlineBusinessInfo {
            it[notifySwitch] = 1
            it[bindUinNotifySwitch] = 1
        }
    }.toByteArray()

    override fun parse(client: LagrangeClient, payload: ByteArray): String =
        RegisterInfoResponse(payload).get { message }
}