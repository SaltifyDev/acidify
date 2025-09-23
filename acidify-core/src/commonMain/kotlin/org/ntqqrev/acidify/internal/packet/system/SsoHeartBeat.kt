package org.ntqqrev.acidify.internal.packet.system

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbSchema

internal object SsoHeartBeat : PbSchema() {
    val type = PbInt32[1]
}