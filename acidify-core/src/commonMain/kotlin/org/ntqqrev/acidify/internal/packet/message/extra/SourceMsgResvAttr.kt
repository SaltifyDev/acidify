package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object SourceMsgResvAttr : PbSchema() {
    val oriMsgType = PbInt32[1]
    val sourceMsgId = PbInt64[2]
    val senderUid = PbString[3]
}