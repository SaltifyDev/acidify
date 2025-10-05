package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.PbOptional
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString
import org.ntqqrev.acidify.pb.get

internal object FriendRequest : PbSchema() {
    val body = PbOptional[Body[1]]

    internal object Body : PbSchema() {
        val fromUid = PbString[2]
        val message = PbString[10]
        val via = PbOptional[PbString[11]]
    }
}

