package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object QSmallFaceExtra : PbSchema() {
    val faceId = PbInt32[1]
    val text = PbString[2]
    val compatText = PbString[3]
}