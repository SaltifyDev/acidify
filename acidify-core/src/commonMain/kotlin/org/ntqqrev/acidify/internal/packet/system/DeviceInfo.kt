package org.ntqqrev.acidify.internal.packet.system

import org.ntqqrev.acidify.pb.PbOptional
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object DeviceInfo : PbSchema() {
    val devName = PbString[1]
    val devType = PbString[2]
    val osVer = PbString[3]
    val brand = PbOptional[PbString[4]]
    val vendorOsName = PbString[5]
}