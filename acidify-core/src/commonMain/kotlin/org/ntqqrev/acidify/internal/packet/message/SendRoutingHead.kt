package org.ntqqrev.acidify.internal.packet.message

import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString
import org.ntqqrev.acidify.pb.get

internal object SendRoutingHead : PbSchema() {
    val c2c = C2C[1]
    val group = Grp[2]
    val trans211 = Trans211[15]

    internal object C2C : PbSchema() {
        val peerUin = PbInt64[1]
        val peerUid = PbString[2]
    }

    internal object Grp : PbSchema() {
        val groupUin = PbInt64[1]
    }
}