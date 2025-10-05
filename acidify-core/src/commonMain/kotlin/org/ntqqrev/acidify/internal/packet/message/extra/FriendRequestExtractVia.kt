package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.PbOptional
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString
import org.ntqqrev.acidify.pb.get

internal object FriendRequestExtractVia : PbSchema() {
    val body = PbOptional[Body[1]]

    internal object Body : PbSchema() {
        val via = PbString[5]
    }
}