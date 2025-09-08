package org.ntqqrev.acidify.internal.packet.system

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbSchema

internal object OnlineBusinessInfo : PbSchema() {
    val notifySwitch = PbInt32[1]
    val bindUinNotifySwitch = PbInt32[2]
}