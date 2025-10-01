package org.ntqqrev.acidify.internal.packet.message.action

import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object SsoGetPeerSeqReq : PbSchema() {
    val peerUid = PbString[1]
}

internal object SsoGetPeerSeqResp : PbSchema() {
    val seq1 = PbInt64[3]
    val seq2 = PbInt64[4]
    val latestMsgTime = PbInt64[5]
}

