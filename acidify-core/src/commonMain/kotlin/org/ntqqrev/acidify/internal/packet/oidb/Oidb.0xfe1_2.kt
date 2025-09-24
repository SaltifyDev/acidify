package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object FetchUserInfoByUinReq : PbSchema() {
    val uin = PbInt64[1]
    val keys = PbRepeated[FetchUserInfoReqKey[3]]
}

internal object FetchUserInfoByUidReq : PbSchema() {
    val uid = PbString[1]
    val keys = PbRepeated[FetchUserInfoReqKey[3]]
}

internal object FetchUserInfoReqKey : PbSchema() {
    val key = PbInt32[1]
}

internal object FetchUserInfoResp : PbSchema() {
    val body = Body[1]

    internal object Body : PbSchema() {
        val properties = Properties[2]
        val uin = PbInt64[3]

        internal object Properties : PbSchema() {
            val numberProps = PbRepeated[NumberProp[1]]
            val stringProps = PbRepeated[StringProp[2]]

            internal object NumberProp : PbSchema() {
                val key = PbInt32[1]
                val value = PbInt32[2]
            }

            internal object StringProp : PbSchema() {
                val key = PbInt32[1]
                val value = PbString[2]
            }
        }
    }
}