package org.ntqqrev.acidify.internal.packet.message.media

import org.ntqqrev.acidify.pb.*

internal object FlashTransferUploadReq : PbSchema() {
    val field1 = PbInt32[1]  // 0
    val appId = PbInt32[2]  // 1402: 私信语音, 1403: 群语音, 1413: 私信视频, 1414: 私信视频封面, 1415: 群视频, 1416: 群视频封面, 1406: 私信图片, 1407: 群聊图片, 14901: 闪传, 14903: 闪传封面
    val field3 = PbInt32[3]  // 2
    val body = FlashTransferUploadBody[107]
}

internal object FlashTransferUploadBody : PbSchema() {
    val field1 = PbBytes[1]  // Empty
    val uKey = PbString[2]
    val start = PbInt32[3]  // Start
    val end = PbInt32[4]  // Start + Size - 1
    val sha1 = PbBytes[5]
    val sha1StateV = FlashTransferSha1StateV[6]
    val body = PbBytes[7]
}

internal object FlashTransferSha1StateV : PbSchema() {
    val state = PbRepeatedBytes[1]
}

internal object FlashTransferUploadResp : PbSchema() {
    val status = PbString[5]
}