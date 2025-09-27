package org.ntqqrev.acidify.internal.packet.message

import org.ntqqrev.acidify.internal.packet.message.elem.*
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.get

internal object Elem : PbSchema() {
    val text = Text[1]
    val notOnlineImage = NotOnlineImage[4]
    val transElemInfo = TransElem[5]
    val customFace = CustomFace[8]
    val richMsg = RichMsg[12]
    val videoFile = VideoFile[19]
    val srcMsg = SourceMsg[45]
    val lightAppElem = LightAppElem[51]
    val commonElem = CommonElem[53]
}