package org.ntqqrev.acidify.internal.packet.message

import org.ntqqrev.acidify.pb.PbBytes
import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema

internal object TmpPtt : PbSchema() {
    val fileType = PbInt32[1]
    val fileUuid = PbBytes[2]
    val fileMd5 = PbBytes[3]
    val fileName = PbBytes[4]
    val fileSize = PbInt32[5]
    val pttTimes = PbInt32[6]
    val userType = PbInt32[7]
    val pttTransFlag = PbInt32[8]
    val busiType = PbInt32[9]
    val msgId = PbInt64[10]
    val pbReserve = PbBytes[30]
    val pttEncodeData = PbBytes[31]
}