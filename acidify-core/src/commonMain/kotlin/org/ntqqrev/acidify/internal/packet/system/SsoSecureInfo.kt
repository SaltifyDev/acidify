package org.ntqqrev.acidify.internal.packet.system

import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.pb

object SsoSecureInfo : PbSchema() {
    val sign = pb bytes 1
    val token = pb bytes 2
    val extra = pb bytes 3
}