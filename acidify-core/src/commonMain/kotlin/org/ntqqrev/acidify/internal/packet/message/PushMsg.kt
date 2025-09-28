package org.ntqqrev.acidify.internal.packet.message

import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.get

internal object PushMsg : PbSchema() {
    val message = CommonMessage[1]
}