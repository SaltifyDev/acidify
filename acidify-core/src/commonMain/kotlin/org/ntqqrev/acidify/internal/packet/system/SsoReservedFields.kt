package org.ntqqrev.acidify.internal.packet.system

import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.pb

object SsoReservedFields : PbSchema() {
    val trace = pb string 15
    val uid = pb.optional string 16
    val secureInfo = pb.optional message SsoSecureInfo field 24
}