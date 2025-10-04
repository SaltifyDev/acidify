package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.internal.packet.message.media.IPv4
import org.ntqqrev.acidify.pb.*

internal object Oidb0xE37Req : PbSchema() {
    val subCommand = PbInt32[1]
    val seq = PbInt32[2]
    val downloadBody = DownloadBody[14]
    val uploadBody = UploadBody[19]
    val field101 = PbInt32[101]
    val field102 = PbInt32[102]
    val field200 = PbInt32[200]
    val field99999 = PbBytes[99999]

    internal object DownloadBody : PbSchema() {
        val receiverUid = PbString[10]
        val fileUuid = PbString[20]
        val type = PbInt32[30]
        val fileHash = PbString[60]
        val t2 = PbInt32[601]
    }

    internal object UploadBody : PbSchema() {
        val senderUid = PbString[10]
        val receiverUid = PbString[20]
        val fileSize = PbInt32[30]
        val fileName = PbString[40]
        val md510MCheckSum = PbBytes[50]
        val sha1CheckSum = PbBytes[60]
        val localPath = PbString[70]
        val md5CheckSum = PbBytes[110]
        val sha3CheckSum = PbBytes[120]
    }
}

internal object Oidb0xE37Resp : PbSchema() {
    val command = PbInt32[1]
    val subCommand = PbInt32[2]
    val downloadBody = DownloadBody[14]
    val uploadBody = UploadBody[19]
    val field50 = PbInt32[50]
    val field101 = PbInt32[101]
    val field102 = PbInt32[102]
    val field200 = PbInt32[200]
    val field99999 = PbBytes[99999]

    internal object DownloadBody : PbSchema() {
        val field10 = PbInt32[10]
        val state = PbString[20]
        val result = DownloadResult[30]

        internal object DownloadResult : PbSchema() {
            val server = PbString[20]
            val port = PbInt32[40]
            val url = PbString[50]
        }
    }

    internal object UploadBody : PbSchema() {
        val retCode = PbInt32[10]
        val retMsg = PbString[20]
        val totalSpace = PbInt64[30]
        val usedSpace = PbInt64[40]
        val uploadedSize = PbInt64[50]
        val uploadIp = PbString[60]
        val uploadDomain = PbString[70]
        val uploadPort = PbInt32[80]
        val uuid = PbString[90]
        val uploadKey = PbBytes[100]
        val boolFileExist = PbBoolean[110]
        val packSize = PbInt32[120]
        val uploadIpList = PbRepeatedString[130]
        val uploadHttpsPort = PbInt32[140]
        val uploadHttpsDomain = PbString[150]
        val uploadDns = PbString[160]
        val uploadLanip = PbString[170]
        val fileIdCrc = PbString[200]
        val rtpMediaPlatformUploadAddress = PbRepeated[IPv4[210]]
        val mediaPlatformUploadKey = PbBytes[220]
    }
}