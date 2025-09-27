package org.ntqqrev.acidify.internal.packet.message

import org.ntqqrev.acidify.pb.PbBytes
import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object Attr : PbSchema() {
    val codePage = PbInt32[1]
    val time = PbInt32[2]
    val playModeRandom = PbInt32[3]
    val color = PbInt32[4]
    val size = PbInt32[5]
    val tabEffect = PbInt32[6]
    val charSet = PbInt32[7]
    val pitchAndFamily = PbInt32[8]
    val fontName = PbString[9]
    val reserveData = PbBytes[10]
}
