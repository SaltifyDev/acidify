package org.ntqqrev.acidify.internal.packet.highway

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbSchema

internal object FileId : PbSchema() {
    val appId = PbInt32[4]
    val ttl = PbInt32[10]
}