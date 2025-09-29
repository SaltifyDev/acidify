package org.ntqqrev.acidify.internal.packet.highway

import org.ntqqrev.acidify.pb.*

internal object MsgInfo : PbSchema() {
    val msgInfoBody = PbRepeated[MsgInfoBody[1]]
    val extBizInfo = ExtBizInfo[2]
}

internal object MsgInfoBody : PbSchema() {
    val index = IndexNode[1]
    val picture = PictureInfo[2]
    val video = VideoInfo[3]
    val audio = AudioInfo[4]
    val fileExist = PbBoolean[5]
    val hashSum = HashSum[6]
}

internal object IndexNode : PbSchema() {
    val info = FileInfo[1]
    val fileUuid = PbString[2]
    val storeId = PbInt32[3]
    val uploadTime = PbInt32[4]
    val ttl = PbInt32[5]
    val subType = PbInt32[6]
}

internal object FileInfo : PbSchema() {
    val fileSize = PbInt32[1]
    val fileHash = PbString[2]
    val fileSha1 = PbString[3]
    val fileName = PbString[4]
    val type = FileType[5]
    val width = PbInt32[6]
    val height = PbInt32[7]
    val time = PbInt32[8]
    val original = PbInt32[9]
}

internal object FileType : PbSchema() {
    val type = PbInt32[1]
    val picFormat = PbInt32[2]
    val videoFormat = PbInt32[3]
    val voiceFormat = PbInt32[4]
}

internal object PictureInfo : PbSchema() {
    val urlPath = PbString[1]
    val ext = PicUrlExtInfo[2]
    val domain = PbString[3]
}

internal object PicUrlExtInfo : PbSchema() {
    val originalParameter = PbString[1]
    val bigParameter = PbString[2]
    val thumbParameter = PbString[3]
}

internal object VideoInfo : PbSchema()

internal object AudioInfo : PbSchema()

internal object HashSum : PbSchema() {
    val c2c = C2cSource[201]
    val troop = TroopSource[202]
}

internal object C2cSource : PbSchema() {
    val friendUid = PbString[2]
}

internal object TroopSource : PbSchema() {
    val groupUin = PbInt32[1]
}

internal object ExtBizInfo : PbSchema() {
    val pic = PicExtBizInfo[1]
    val video = VideoExtBizInfo[2]
    val ptt = PttExtBizInfo[3]
    val busiType = PbInt32[10]
}

internal object PicExtBizInfo : PbSchema() {
    val bizType = PbInt32[1]
    val textSummary = PbString[2]
}

internal object VideoExtBizInfo : PbSchema() {
    val fromScene = PbInt32[1]
    val toScene = PbInt32[2]
    val bytesPbReserve = PbBytes[3]
}

internal object PttExtBizInfo : PbSchema() {
    val srcUin = PbInt64[1]
    val pttScene = PbInt32[2]
    val pttType = PbInt32[3]
    val changeVoice = PbInt32[4]
    val waveform = PbBytes[5]
    val autoConvertText = PbInt32[6]
    val bytesReserve = PbBytes[11]
    val bytesPbReserve = PbBytes[12]
    val bytesGeneralFlags = PbBytes[13]
}