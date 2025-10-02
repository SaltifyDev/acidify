package org.ntqqrev.acidify.internal.packet.message.action

import org.ntqqrev.acidify.pb.*

/**
 * trpc.msg.msg_svc.MsgService.SsoReadedReport
 */
internal object SsoReadedReportReq : PbSchema() {
    val group = PbOptional[SsoReadedReportGroup[1]]
    val c2c = PbOptional[SsoReadedReportC2C[2]]
}

internal object SsoReadedReportC2C : PbSchema() {
    val targetUid = PbString[2]
    val time = PbInt64[3]
    val startSequence = PbInt64[4]
}

internal object SsoReadedReportGroup : PbSchema() {
    val groupUin = PbInt64[1]
    val startSequence = PbInt64[2]
}

internal object SsoReadedReportResp : PbSchema() {
    // Empty response
}

