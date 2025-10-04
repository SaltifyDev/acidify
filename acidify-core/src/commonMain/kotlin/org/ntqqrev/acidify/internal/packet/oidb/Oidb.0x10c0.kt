package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object FetchGroupNotificationsReq : PbSchema() {
    val count = PbInt32[1]
    val startSeq = PbInt64[2]
}

internal object FetchGroupNotificationsResp : PbSchema() {
    val notifications = PbRepeated[GroupNotification[1]]
    val nextStartSeq = PbInt64[2]
}

internal object GroupNotification : PbSchema() {
    val sequence = PbInt64[1]
    val notifyType = PbInt32[2]
    val requestState = PbInt32[3]
    val group = Group[4]
    val user1 = User[5]
    val user2 = PbOptional[User[6]]
    val user3 = PbOptional[User[7]]
    val time = PbInt32[8]
    val comment = PbString[10]

    internal object User : PbSchema() {
        val uid = PbString[1]
        val nickname = PbString[2]
    }

    internal object Group : PbSchema() {
        val groupUin = PbInt64[1]
        val groupName = PbString[2]
    }
}