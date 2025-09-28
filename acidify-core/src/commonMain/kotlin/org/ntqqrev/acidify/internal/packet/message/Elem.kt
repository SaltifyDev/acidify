package org.ntqqrev.acidify.internal.packet.message

import org.ntqqrev.acidify.internal.packet.message.elem.*
import org.ntqqrev.acidify.pb.PbOptional
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.get

internal object Elem : PbSchema() {
    val text = PbOptional[Text[1]]
    val notOnlineImage = PbOptional[NotOnlineImage[4]]
    val transElemInfo = PbOptional[TransElem[5]]
    val customFace = PbOptional[CustomFace[8]]
    val richMsg = PbOptional[RichMsg[12]]
    val videoFile = PbOptional[VideoFile[19]]
    val srcMsg = PbOptional[SourceMsg[45]]
    val lightAppElem = PbOptional[LightAppElem[51]]
    val commonElem = PbOptional[CommonElem[53]]
}