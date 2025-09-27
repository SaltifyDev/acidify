package org.ntqqrev.acidify.internal.packet.message

import org.ntqqrev.acidify.pb.*

internal object Ptt : PbSchema() {
    val fileType = PbInt32[1]
    val srcUin = PbInt64[2]
    val fileUuid = PbBytes[3]
    val fileMd5 = PbBytes[4]
    val fileName = PbString[5]
    val fileSize = PbInt32[6]
    val reserve = PbBytes[7]
    val fileId = PbInt32[8]
    val serverIp = PbInt32[9]
    val serverPort = PbInt32[10]
    val valid = PbBoolean[11]
    val signature = PbBytes[12]
    val shortcut = PbBytes[13]
    val fileKey = PbString[14]
    val magicPttIndex = PbInt32[15]
    val voiceSwitch = PbInt32[16]
    val pttUrl = PbBytes[17]
    val groupFileKey = PbBytes[18]
    val time = PbInt32[19]
    val downPara = PbBytes[20]
    val format = PbInt32[29]
    val pbReserve = PbBytes[30]
    val pttUrls = PbRepeatedBytes[31]
    val downloadFlag = PbInt32[32]
}