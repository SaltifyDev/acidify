package org.ntqqrev.acidify.internal.packet.message

import org.ntqqrev.acidify.pb.PbRepeated
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.get

internal object RichText : PbSchema() {
    val attr = Attr[1]
    val elems = PbRepeated[Elem[2]]
    val notOnlineFile = NotOnlineFile[3]
    val ptt = Ptt[4]
    val tmpPtt = TmpPtt[5]
    val trans211TmpMsg = Trans211TmpMsg[6]
}