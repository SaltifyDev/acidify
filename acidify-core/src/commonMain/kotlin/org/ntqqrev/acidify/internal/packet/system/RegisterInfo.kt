package org.ntqqrev.acidify.internal.packet.system

import org.ntqqrev.acidify.pb.*

internal object RegisterInfo : PbSchema() {
    val guid = PbString[1]
    val kickPc = PbOptional[PbBoolean[2]]
    val currentVersion = PbString[3]
    val isFirstRegisterProxyOnline = PbOptional[PbBoolean[4]]
    val localeId = PbOptional[PbInt32[5]]
    val device = DeviceInfo[6]
    val setMute = PbOptional[PbInt32[7]]
    val registerVendorType = PbOptional[PbInt32[8]]
    val regType = PbOptional[PbInt32[9]]
    val businessInfo = OnlineBusinessInfo[10]
    val batteryStatus = PbOptional[PbInt32[11]]
}

