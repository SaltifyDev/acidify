package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.get

internal object SetGroupWholeMuteReq : PbSchema() {
    val groupCode = PbInt64[1]
    val state = State[9]

    internal object State : PbSchema() {
        val isMute = PbInt32[17]
    }
}

