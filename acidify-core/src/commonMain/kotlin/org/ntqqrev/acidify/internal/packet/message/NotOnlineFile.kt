package org.ntqqrev.acidify.internal.packet.message

import org.ntqqrev.acidify.pb.*

internal object NotOnlineFile : PbSchema() {
    val fileType = PbInt32[1]
    val sig = PbBytes[2]
    val fileUuid = PbString[3]
    val fileMd5 = PbBytes[4]
    val fileName = PbString[5]
    val fileSize = PbInt64[6]
    val note = PbBytes[7]
    val reserved = PbInt32[8]
    val subCmd = PbInt32[9]
    val microCloud = PbInt32[10]
    val fileUrls = PbRepeatedBytes[11]
    val downloadFlag = PbInt32[12]
    val dangerLevel = PbInt32[50]
    val lifeTime = PbInt32[51]
    val uploadTime = PbInt32[52]
    val absFileType = PbInt32[53]
    val clientType = PbInt32[54]
    val expireTime = PbInt32[55]
    val pbReserve = PbBytes[56]
    val fileIdCrcMedia = PbString[57]
}