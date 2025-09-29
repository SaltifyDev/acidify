package org.ntqqrev.acidify.internal.packet.highway

import org.ntqqrev.acidify.pb.*

internal object NTV2RichMediaReq : PbSchema() {
    val reqHead = MultiMediaReqHead[1]
    val upload = UploadReq[2]
    val download = DownloadReq[3]
    val downloadRKey = DownloadRKeyReq[4]
    val delete = DeleteReq[5]
    val uploadCompleted = UploadCompletedReq[6]
    val msgInfoAuth = MsgInfoAuthReq[7]
    val uploadKeyRenewal = UploadKeyRenewalReq[8]
    val downloadSafe = DownloadSafeReq[9]
    val extension = PbBytes[99]
}

internal object MultiMediaReqHead : PbSchema() {
    val common = CommonHead[1]
    val scene = SceneInfo[2]
    val client = ClientMeta[3]
}

internal object CommonHead : PbSchema() {
    val requestId = PbInt32[1]
    val command = PbInt32[2]
}

internal object SceneInfo : PbSchema() {
    val requestType = PbInt32[101]
    val businessType = PbInt32[102]
    val sceneType = PbInt32[200]
    val c2C = C2CUserInfo[201]
    val group = GroupInfo[202]
}

internal object C2CUserInfo : PbSchema() {
    val accountType = PbInt32[1]
    val targetUid = PbString[2]
}

internal object GroupInfo : PbSchema() {
    val groupUin = PbInt64[1]
}

internal object ClientMeta : PbSchema() {
    val agentType = PbInt32[1]
}

internal object DownloadReq : PbSchema() {
    val node = IndexNode[1]
    val download = DownloadExt[2]
}

internal object DownloadExt : PbSchema() {
    val pic = PicDownloadExt[1]
    val video = VideoDownloadExt[2]
    val ptt = PttDownloadExt[3]
}

internal object VideoDownloadExt : PbSchema() {
    val busiType = PbInt32[1]
    val sceneType = PbInt32[2]
    val subBusiType = PbInt32[3]
}

internal object PicDownloadExt : PbSchema()

internal object PttDownloadExt : PbSchema()

internal object MsgInfoAuthReq : PbSchema() {
    val msg = PbBytes[1]
    val authTime = PbInt64[2]
}

internal object UploadCompletedReq : PbSchema() {
    val srvSendMsg = PbBoolean[1]
    val clientRandomId = PbInt64[2]
    val msgInfo = MsgInfo[3]
    val clientSeq = PbInt32[4]
}

internal object DeleteReq : PbSchema() {
    val index = PbRepeated[IndexNode[1]]
    val needRecallMsg = PbBoolean[2]
    val msgSeq = PbInt64[3]
    val msgRandom = PbInt64[4]
    val msgTime = PbInt64[5]
}

internal object DownloadRKeyReq : PbSchema() {
    val types = PbRepeatedInt32[1]
}

internal object UploadInfo : PbSchema() {
    val fileInfo = FileInfo[1]
    val subFileType = PbInt32[2]
}

internal object UploadReq : PbSchema() {
    val uploadInfo = PbRepeated[UploadInfo[1]]
    val tryFastUploadCompleted = PbBoolean[2]
    val srvSendMsg = PbBoolean[3]
    val clientRandomId = PbInt64[4]
    val compatQMsgSceneType = PbInt32[5]
    val extBizInfo = ExtBizInfo[6]
    val clientSeq = PbInt32[7]
    val noNeedCompatMsg = PbBoolean[8]
}

internal object UploadKeyRenewalReq : PbSchema() {
    val oldUKey = PbString[1]
    val subType = PbInt32[2]
}

internal object DownloadSafeReq : PbSchema() {
    val index = IndexNode[1]
}

