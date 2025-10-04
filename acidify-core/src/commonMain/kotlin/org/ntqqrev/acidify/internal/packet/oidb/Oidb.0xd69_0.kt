package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object FetchFilteredFriendRequestsReq : PbSchema() {
    val field1 = PbInt32[1]
    val field2 = FilteredRequestCount[2]
}

internal object FilteredRequestCount : PbSchema() {
    val count = PbInt32[1]
}

internal object FetchFilteredFriendRequestsResp : PbSchema() {
    val info = FilteredFriendRequestInfo[2]
}

internal object FilteredFriendRequestInfo : PbSchema() {
    val requests = PbRepeated[FilteredFriendRequestItem[1]]
}

internal object FilteredFriendRequestItem : PbSchema() {
    val sourceUid = PbString[1]
    val sourceNickname = PbString[2]
    val comment = PbString[5]
    val source = PbString[6]
    val warningInfo = PbString[7]
    val timestamp = PbInt64[8]
}
