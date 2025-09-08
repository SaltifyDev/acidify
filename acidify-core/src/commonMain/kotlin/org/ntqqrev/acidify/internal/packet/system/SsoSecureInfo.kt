package org.ntqqrev.acidify.internal.packet.system

import org.ntqqrev.acidify.pb.PbBytes
import org.ntqqrev.acidify.pb.PbSchema

internal object SsoSecureInfo : PbSchema() {
    val sign = PbBytes[1]
    val token = PbBytes[2]
    val extra = PbBytes[3]
}