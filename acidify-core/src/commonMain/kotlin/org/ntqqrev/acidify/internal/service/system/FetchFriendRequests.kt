package org.ntqqrev.acidify.internal.service.system

import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.*
import org.ntqqrev.acidify.internal.service.OidbService
import org.ntqqrev.acidify.pb.PbObject
import org.ntqqrev.acidify.pb.invoke

internal abstract class FetchFriendRequests<R>(oidbCommand: Int, oidbService: Int, val isFiltered: Boolean) :
    OidbService<Int, R>(oidbCommand, oidbService) {
    object Normal : FetchFriendRequests<List<PbObject<FriendRequestItem>>>(0x5cf, 11, false) {
        override fun buildOidb(client: LagrangeClient, payload: Int): ByteArray =
            FetchFriendRequestsReq {
                it[version] = 1
                it[type] = 6
                it[selfUid] = client.sessionStore.uid
                it[startIndex] = 0
                it[reqNum] = payload
                it[getFlag] = 2
                it[startTime] = 0
                it[needCommFriend] = 1
                it[field22] = 1
            }.toByteArray()

        override fun parseOidb(client: LagrangeClient, payload: ByteArray): List<PbObject<FriendRequestItem>> {
            val resp = FetchFriendRequestsResp(payload)
            val info = resp.get { info }
            return info.get { requests }
        }
    }

    object Filtered : FetchFriendRequests<List<PbObject<FilteredFriendRequestItem>>>(0xd69, 0, true) {
        override fun buildOidb(client: LagrangeClient, payload: Int): ByteArray =
            FetchFilteredFriendRequestsReq {
                it[field1] = 1
                it[field2] = FilteredRequestCount {
                    it[count] = payload
                }
            }.toByteArray()

        override fun parseOidb(client: LagrangeClient, payload: ByteArray): List<PbObject<FilteredFriendRequestItem>> {
            val resp = FetchFilteredFriendRequestsResp(payload)
            val info = resp.get { info }
            return info.get { requests }
        }
    }
}
