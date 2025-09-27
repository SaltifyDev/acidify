package org.ntqqrev.acidify.internal.packet.message

import org.ntqqrev.acidify.pb.PbBytes
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.get

internal object MessageBody : PbSchema() {
    val richText = RichText[1]
    val msgContent = PbBytes[2]
    val msgEncryptContent = PbBytes[3]
}