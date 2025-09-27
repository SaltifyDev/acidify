package org.ntqqrev.acidify.internal.packet.message.elem

import org.ntqqrev.acidify.pb.*

internal object CustomFace : PbSchema() {
    val guid = PbBytes[1]
    val filePath = PbString[2]
    val shortcut = PbString[3]
    val buffer = PbBytes[4]
    val flag = PbBytes[5]
    val oldData = PbBytes[6]
    val fileId = PbInt32[7]
    val serverIp = PbInt32[8]
    val serverPort = PbInt32[9]
    val fileType = PbInt32[10]
    val signature = PbBytes[11]
    val useful = PbInt32[12]
    val md5 = PbBytes[13]
    val thumbUrl = PbString[14]
    val bigUrl = PbString[15]
    val origUrl = PbString[16]
    val bizType = PbInt32[17]
    val repeatIndex = PbInt32[18]
    val repeatImage = PbInt32[19]
    val imageType = PbInt32[20]
    val index = PbInt32[21]
    val width = PbInt32[22]
    val height = PbInt32[23]
    val source = PbInt32[24]
    val size = PbInt32[25]
    val origin = PbInt32[26]
    val thumbWidth = PbInt32[27]
    val thumbHeight = PbInt32[28]
    val showLen = PbInt32[29]
    val downloadLen = PbInt32[30]
    val x400Url = PbString[31]
    val x400Width = PbInt32[32]
    val x400Height = PbInt32[33]
    val pbReserve = PbReserve1[34]

    internal object PbReserve1 : PbSchema() {
        val subType = PbInt32[1]
        val field3 = PbInt32[3]
        val field4 = PbInt32[4]
        val summary = PbString[9]
        val field10 = PbInt32[10]
        val field21 = PbReserve2[21]
        val field31 = PbString[31]

        internal object PbReserve2 : PbSchema() {
            val field1 = PbInt32[1]
            val field2 = PbString[2]
            val field3 = PbInt32[3]
            val field4 = PbInt32[4]
            val field5 = PbInt32[5]
            val md5Str = PbString[7]
        }
    }
}