package org.ntqqrev.acidify.internal.packet.highway

import org.ntqqrev.acidify.pb.*

internal object C501ReqBody : PbSchema() {
    val reqBody = SubCmd0x501ReqBody[1281]
}

internal object C501RspBody : PbSchema() {
    val rspBody = SubCmd0x501RspBody[1281]
}

internal object SubCmd0x501ReqBody : PbSchema() {
    val uin = PbInt64[1]
    val idcId = PbInt32[2]
    val appid = PbInt32[3]
    val loginSigType = PbInt32[4]
    val loginSigTicket = PbBytes[5]
    val requestFlag = PbInt32[6]
    val serviceTypes = PbRepeatedInt32[7]
    val bid = PbInt32[8]
    val field9 = PbInt32[9]
    val field10 = PbInt32[10]
    val field11 = PbInt32[11]
    val version = PbString[15]
}

internal object SubCmd0x501RspBody : PbSchema() {
    val sigSession = PbBytes[1]
    val sessionKey = PbBytes[2]
    val addrs = PbRepeated[SrvAddrs[3]]
}

internal object SrvAddrs : PbSchema() {
    val serviceType = PbInt32[1]
    val addrs = PbRepeated[IpAddr[2]]
}

internal object IpAddr : PbSchema() {
    val type = PbInt32[1]
    val ip = PbFixed32[2]
    val port = PbInt32[3]
    val area = PbInt32[4]
}


