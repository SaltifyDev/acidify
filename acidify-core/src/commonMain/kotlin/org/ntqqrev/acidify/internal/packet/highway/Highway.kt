package org.ntqqrev.acidify.internal.packet.highway

import org.ntqqrev.acidify.pb.*

internal object ReqDataHighwayHead : PbSchema() {
    val msgBaseHead = DataHighwayHead[1]
    val msgSegHead = SegHead[2]
    val bytesReqExtendInfo = PbBytes[3]
    val timestamp = PbInt64[4]
    val msgLoginSigHead = LoginSigHead[5]
}

internal object RespDataHighwayHead : PbSchema() {
    val msgBaseHead = DataHighwayHead[1]
    val msgSegHead = SegHead[2]
    val errorCode = PbInt32[3]
    val allowRetry = PbInt32[4]
    val cacheCost = PbInt32[5]
    val htCost = PbInt32[6]
    val bytesRspExtendInfo = PbBytes[7]
    val timestamp = PbInt64[8]
    val range = PbInt64[9]
    val isReset = PbInt32[10]
}

internal object DataHighwayHead : PbSchema() {
    val version = PbInt32[1]
    val uin = PbString[2]
    val command = PbString[3]
    val seq = PbInt32[4]
    val retryTimes = PbInt32[5]
    val appId = PbInt32[6]
    val dataFlag = PbInt32[7]
    val commandId = PbInt32[8]
    val buildVer = PbBytes[9]
}

internal object LoginSigHead : PbSchema() {
    val uint32LoginSigType = PbInt32[1]
    val bytesLoginSig = PbBytes[2]
    val appId = PbInt32[3]
}

internal object SegHead : PbSchema() {
    val serviceId = PbInt32[1]
    val filesize = PbInt64[2]
    val dataOffset = PbInt64[3]
    val dataLength = PbInt32[4]
    val retCode = PbInt32[5]
    val serviceTicket = PbBytes[6]
    val md5 = PbBytes[8]
    val fileMd5 = PbBytes[9]
    val cacheAddr = PbInt32[10]
    val cachePort = PbInt32[13]
}