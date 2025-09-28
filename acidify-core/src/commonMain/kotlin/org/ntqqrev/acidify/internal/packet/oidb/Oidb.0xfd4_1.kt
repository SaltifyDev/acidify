package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object IncPull : PbSchema() {
    val reqCount = PbInt32[2]
    val time = PbInt64[3]
    val localSeq = PbInt32[4]
    val cookie = FetchFriendsCookie[5]
    val flag = PbInt32[6]
    val proxySeq = PbInt32[7]
    val requestBiz = PbRepeated[Biz[10001]]
    val extSnsFlagKey = PbRepeatedInt32[10002]
    val extPrivateIdListKey = PbRepeatedInt32[10003]

    internal object Biz : PbSchema() {
        val bizType = PbInt32[1]
        val bizData = Busi[2]

        internal object Busi : PbSchema() {
            val extBusi = PbRepeatedInt32[1]
        }
    }
}

internal object IncPullResp : PbSchema() {
    val seq = PbInt32[1]
    val cookie = PbOptional[FetchFriendsCookie[2]]
    val isEnd = PbBoolean[3]
    val time = PbInt64[6]
    val selfUin = PbInt64[7]
    val smallSeq = PbInt32[8]
    val friendList = PbRepeated[Friend[101]]
    val category = PbRepeated[Category[102]]

    internal object Friend : PbSchema() {
        val uid = PbString[1]
        val categoryId = PbInt32[2]
        val uin = PbInt64[3]
        val subBizMap = PbRepeated[SubBizEntry[10001]]

        internal object SubBizEntry : PbSchema() {
            val key = PbInt32[1]
            val value = SubBiz[2]
        }

        internal object SubBiz : PbSchema() {
            val numDataMap = PbRepeated[NumDataEntry[1]]
            val dataMap = PbRepeated[DataEntry[2]]

            internal object NumDataEntry : PbSchema() {
                val key = PbInt32[1]
                val value = PbInt32[2]
            }

            internal object DataEntry : PbSchema() {
                val key = PbInt32[1]
                val value = PbString[2]
            }
        }
    }

    internal object Category : PbSchema() {
        val categoryId = PbInt32[1]
        val categoryName = PbString[2]
        val categoryMemberCount = PbInt32[3]
        val categorySortId = PbInt32[4]
    }
}

internal object FetchFriendsCookie : PbSchema() {
    val nextUin = PbOptional[PbInt64[1]]
}