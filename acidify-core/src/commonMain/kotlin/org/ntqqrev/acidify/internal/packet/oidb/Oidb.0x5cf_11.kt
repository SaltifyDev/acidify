package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object FetchFriendRequestsReq : PbSchema() {
    val version = PbInt32[1]
    val type = PbInt32[3]
    val selfUid = PbString[4]
    val startIndex = PbInt32[5]
    val reqNum = PbInt32[6]
    val getFlag = PbInt32[8]
    val startTime = PbInt32[9]
    val needCommFriend = PbInt32[12]
    val field22 = PbInt32[22]
}

internal object FetchFriendRequestsResp : PbSchema() {
    val field1 = PbInt32[1]
    val field2 = PbInt32[2]
    val info = FriendRequestInfo[3]
}

internal object FriendRequestInfo : PbSchema() {
    val field2 = PbInt32[2]
    val count = PbInt32[3]
    val requests = PbRepeated[FriendRequestItem[7]]
}

internal object FriendRequestItem : PbSchema() {
    val targetUid = PbString[1]
    val sourceUid = PbString[2]
    val state = PbInt32[3]
    val timestamp = PbInt64[4]
    val comment = PbString[5]
    val source = PbString[6]
    val sourceId = PbInt32[7]
    val subSourceId = PbInt32[8]
}
