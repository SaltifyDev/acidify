package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object FetchClientKeyResp : PbSchema() {
    val clientKey = PbString[3]
    val expireTime = PbInt64[4]
}