package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString
import org.ntqqrev.acidify.pb.get

internal object PrivateFileDownloadReq : PbSchema() {
    val body = PrivateFileDownloadReqBody[1]
}

internal object PrivateFileDownloadReqBody : PbSchema() {
    val receiverUid = PbString[1]
    val fileUuid = PbString[2]
    val fileHash = PbString[3]
    val t2 = PbInt32[4]
}

internal object PrivateFileDownloadResp : PbSchema() {
    val body = PrivateFileDownloadRespBody[1]
}

internal object PrivateFileDownloadRespBody : PbSchema() {
    val result = PrivateFileDownloadResult[2]
}

internal object PrivateFileDownloadResult : PbSchema() {
    val server = PbString[4]
    val port = PbInt32[5]
    val url = PbString[6]
}