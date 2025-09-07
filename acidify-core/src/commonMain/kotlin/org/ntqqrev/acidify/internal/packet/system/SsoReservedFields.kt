package org.ntqqrev.acidify.internal.packet.system

import org.ntqqrev.acidify.pb.PbOptional
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString
import org.ntqqrev.acidify.pb.get

object SsoReservedFields : PbSchema() {
    val trace = PbString[15]
    val uid = PbOptional[PbString[5]]
    val secureInfo = PbOptional[SsoSecureInfo[24]]
}