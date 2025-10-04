package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object Oidb0x6D6Req : PbSchema() {
    val uploadFile = UploadFile[1]
    val downloadFile = DownloadFile[3]
    val broadcastFile = BroadcastFile[5]

    internal object UploadFile : PbSchema() {
        val groupUin = PbInt64[1]
        val appId = PbInt32[2]
        val busId = PbInt32[3]
        val entrance = PbInt32[4]
        val parentFolderId = PbString[5]
        val fileName = PbString[6]
        val localPath = PbString[7]
        val fileSize = PbInt64[8]
        val sha = PbBytes[9]
        val sha3 = PbBytes[10]
        val md5 = PbBytes[11]
        val supportMultiUpload = PbBoolean[12]
    }

    internal object DownloadFile : PbSchema() {
        val groupUin = PbInt64[1]
        val appId = PbInt32[2]
        val busId = PbInt32[3]
        val fileId = PbString[4]
    }

    internal object BroadcastFile : PbSchema() {
        val groupUin = PbInt64[1]
        val type = PbInt32[2]  // 2
        val info = Info[3]

        internal object Info : PbSchema() {
            val busiType = PbInt32[1]  // 102
            val fileId = PbString[2]
            val field3 = PbInt32[3]  // random
            val field4 = PbString[4]  // nullable
            val field5 = PbBoolean[5]  // true
        }
    }
}

internal object Oidb0x6D6Resp : PbSchema() {
    val uploadFile = UploadFile[1]
    val downloadFile = DownloadFile[3]

    internal object UploadFile : PbSchema() {
        val retCode = PbInt32[1]
        val retMsg = PbString[2]
        val clientWording = PbString[3]
        val uploadIp = PbString[4]
        val serverDns = PbString[5]
        val busId = PbInt32[6]
        val fileId = PbString[7]
        val fileKey = PbBytes[8]
        val checkKey = PbBytes[9]
        val fileExist = PbBoolean[10]
        val uploadIpLanV4 = PbRepeatedString[12]
        val uploadIpLanV6 = PbRepeatedString[13]
        val uploadPort = PbInt32[14]
    }

    internal object DownloadFile : PbSchema() {
        val downloadDns = PbString[5]
        val downloadUrl = PbBytes[6]
    }
}