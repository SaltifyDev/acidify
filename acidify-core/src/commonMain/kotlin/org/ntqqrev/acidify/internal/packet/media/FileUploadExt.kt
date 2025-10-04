package org.ntqqrev.acidify.internal.packet.media

import org.ntqqrev.acidify.pb.*

/**
 * 文件上传额外信息
 */
internal object FileUploadExt : PbSchema() {
    val unknown1 = PbInt32[1]
    val unknown2 = PbInt32[2]
    val unknown3 = PbInt32[3]
    val entry = FileUploadEntry[100]
    val unknown200 = PbInt32[200]
}

internal object FileUploadEntry : PbSchema() {
    val busiBuff = ExcitingBusiInfo[100]
    val fileEntry = ExcitingFileEntry[200]
    val clientInfo = ExcitingClientInfo[300]
    val fileNameInfo = ExcitingFileNameInfo[400]
    val host = ExcitingHostConfig[500]
}

internal object ExcitingBusiInfo : PbSchema() {
    val busId = PbInt32[1]
    val senderUin = PbInt64[100]
    val receiverUin = PbInt64[200]
    val groupCode = PbInt64[400]
}

internal object ExcitingFileEntry : PbSchema() {
    val fileSize = PbInt64[100]
    val md5 = PbBytes[200]
    val checkKey = PbBytes[300]
    val md510M = PbBytes[400]
    val sha3 = PbBytes[500]
    val fileId = PbString[600]
    val uploadKey = PbBytes[700]
}

internal object ExcitingClientInfo : PbSchema() {
    val clientType = PbInt32[100]
    val appId = PbString[200]
    val terminalType = PbInt32[300]
    val clientVer = PbString[400]
    val unknown = PbInt32[600]
}

internal object ExcitingFileNameInfo : PbSchema() {
    val fileName = PbString[100]
}

internal object ExcitingHostConfig : PbSchema() {
    val hosts = PbRepeated[ExcitingHostInfo[200]]
}

internal object ExcitingHostInfo : PbSchema() {
    val url = ExcitingUrlInfo[1]
    val port = PbInt32[2]
}

internal object ExcitingUrlInfo : PbSchema() {
    val unknown = PbInt32[1]
    val host = PbString[2]
}