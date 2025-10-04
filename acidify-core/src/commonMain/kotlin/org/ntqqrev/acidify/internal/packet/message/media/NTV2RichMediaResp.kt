package org.ntqqrev.acidify.internal.packet.message.media

import org.ntqqrev.acidify.pb.*

internal object NTV2RichMediaResp : PbSchema() {
    val respHead = MultiMediaRespHead[1]
    val upload = UploadResp[2]
    val download = DownloadResp[3]
    val downloadRKey = DownloadRKeyResp[4]
    val delete = DeleteResp[5]
    val uploadCompleted = UploadCompletedResp[6]
    val msgInfoAuth = MsgInfoAuthResp[7]
    val uploadKeyRenewal = UploadKeyRenewalResp[8]
    val downloadSafe = DownloadSafeResp[9]
    val extension = PbBytes[99]
}

internal object MultiMediaRespHead : PbSchema() {
    val common = CommonHead[1]
    val retCode = PbInt32[2]
    val message = PbString[3]
}

internal object DownloadResp : PbSchema() {
    val rKeyParam = PbString[1]
    val rKeyTtlSecond = PbInt32[2]
    val info = DownloadInfo[3]
    val rKeyCreateTime = PbInt32[4]
}

internal object DownloadInfo : PbSchema() {
    val domain = PbString[1]
    val urlPath = PbString[2]
    val httpsPort = PbInt32[3]
    val iPv4s = PbRepeated[IPv4[4]]
    val iPv6s = PbRepeated[IPv6[5]]
    val picUrlExtInfo = PicUrlExtInfo[6]
    val videoExtInfo = VideoExtInfo[7]
}

internal object VideoExtInfo : PbSchema() {
    val videoCodecFormat = PbInt32[1]
}

internal object IPv4 : PbSchema() {
    val outIP = PbInt32[1]
    val outPort = PbInt32[2]
    val inIP = PbInt32[3]
    val inPort = PbInt32[4]
    val iPType = PbInt32[5]
}

internal object IPv6 : PbSchema() {
    val outIP = PbBytes[1]
    val outPort = PbInt32[2]
    val inIP = PbBytes[3]
    val inPort = PbInt32[4]
    val iPType = PbInt32[5]
}

internal object UploadResp : PbSchema() {
    val uKey = PbString[1]
    val uKeyTtlSecond = PbInt32[2]
    val iPv4s = PbRepeated[IPv4[3]]
    val iPv6s = PbRepeated[IPv6[4]]
    val msgSeq = PbInt64[5]
    val msgInfo = MsgInfo[6]
    val ext = PbRepeated[RichMediaStorageTransInfo[7]]
    val compatQMsg = PbBytes[8]
    val subFileInfos = PbRepeated[SubFileInfo[10]]
}

internal object RichMediaStorageTransInfo : PbSchema() {
    val subType = PbInt32[1]
    val extType = PbInt32[2]
    val extValue = PbBytes[3]
}

internal object SubFileInfo : PbSchema() {
    val subType = PbInt32[1]
    val uKey = PbString[2]
    val uKeyTtlSecond = PbInt32[3]
    val iPv4s = PbRepeated[IPv4[4]]
    val iPv6s = PbRepeated[IPv6[5]]
}

internal object DownloadRKeyResp : PbSchema() {
    val rKeys = PbRepeated[RKeyInfo[1]]
}

internal object RKeyInfo : PbSchema() {
    val rkey = PbString[1]
    val rkeyTtlSec = PbInt64[2]
    val storeId = PbInt32[3]
    val rkeyCreateTime = PbInt32[4]
    val type = PbInt32[5]
}

internal object UploadCompletedResp : PbSchema() {
    val msgSeq = PbInt64[1]
}

internal object MsgInfoAuthResp : PbSchema() {
    val authCode = PbInt32[1]
    val msg = PbBytes[2]
    val resultTime = PbInt64[3]
}

internal object UploadKeyRenewalResp : PbSchema() {
    val ukey = PbString[1]
    val ukeyTtlSec = PbInt64[2]
}

internal object DownloadSafeResp : PbSchema()

internal object DeleteResp : PbSchema()

