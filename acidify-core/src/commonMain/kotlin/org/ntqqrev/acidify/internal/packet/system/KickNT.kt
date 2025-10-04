package org.ntqqrev.acidify.internal.packet.system

import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object KickNT : PbSchema() {
    val tip = PbString[3]
    val title = PbString[4]
}