package org.ntqqrev.acidify.internal.util

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.system.DeviceInfo
import org.ntqqrev.acidify.pb.invoke

internal fun LagrangeClient.generateDeviceInfo() = DeviceInfo {
    it[devName] = sessionStore.deviceName
    it[devType] = appInfo.kernel
    it[osVer] = "Windows 10.0.19042"
    it[vendorOsName] = appInfo.vendorOs
}