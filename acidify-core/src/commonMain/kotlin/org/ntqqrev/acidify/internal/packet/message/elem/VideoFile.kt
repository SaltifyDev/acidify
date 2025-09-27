package org.ntqqrev.acidify.internal.packet.message.elem

import org.ntqqrev.acidify.pb.*

internal object VideoFile : PbSchema() {
    val fileUuid = PbString[1]
    val fileMd5 = PbBytes[2]
    val fileName = PbString[3]
    val fileFormat = PbInt32[4]
    val fileTime = PbInt32[5]
    val fileSize = PbInt32[6]
    val thumbWidth = PbInt32[7]
    val thumbHeight = PbInt32[8]
    val thumbFileMd5 = PbBytes[9]
    val source = PbBytes[10]
    val thumbFileSize = PbInt32[11]
    val busiType = PbInt32[12]
    val fromChatType = PbInt32[13]
    val toChatType = PbInt32[14]
    val boolSupportProgressive = PbBoolean[15]
    val fileWidth = PbInt32[16]
    val fileHeight = PbInt32[17]
    val subBusiType = PbInt32[18]
    val videoAttr = PbInt32[19]
    val bytesThumbFileUrls = PbRepeatedBytes[20]
    val bytesVideoFileUrls = PbRepeatedBytes[21]
    val thumbDownloadFlag = PbInt32[22]
    val videoDownloadFlag = PbInt32[23]
    val pbReserve = PbBytes[24]
}