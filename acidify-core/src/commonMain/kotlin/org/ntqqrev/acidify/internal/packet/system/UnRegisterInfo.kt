package org.ntqqrev.acidify.internal.packet.system

import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.get

object UnRegisterInfo : PbSchema() {
    val device = DeviceInfo[2]
}