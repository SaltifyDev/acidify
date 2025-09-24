package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object FetchGroupMembersReq : PbSchema() {
    val groupUin = PbInt64[1]
    val field2 = PbInt32[2]
    val field3 = PbInt32[3]
    val body = Body[4]
    val cookie = PbOptional[PbBytes[15]]

    internal object Body : PbSchema() {
        val memberName = PbBoolean[10]
        val memberCard = PbBoolean[11]
        val level = PbBoolean[12]
        val field13 = PbBoolean[13]
        val field16 = PbBoolean[16]
        val specialTitle = PbBoolean[17]
        val field18 = PbBoolean[18]
        val field20 = PbBoolean[20]
        val field21 = PbBoolean[21]
        val joinTimestamp = PbBoolean[100]
        val lastMsgTimestamp = PbBoolean[101]
        val shutUpTimestamp = PbBoolean[102]
        val field103 = PbBoolean[103]
        val field104 = PbBoolean[104]
        val field105 = PbBoolean[105]
        val field106 = PbBoolean[106]
        val permission = PbBoolean[107]
        val field200 = PbBoolean[200]
        val field201 = PbBoolean[201]
    }
}

internal object FetchGroupMembersResp : PbSchema() {
    val groupUin = PbInt64[1]
    val members = PbRepeated[Member[2]]
    val memberCount = PbInt32[3]
    val memberListChangeSeq = PbInt32[5]
    val memberCardSeq = PbInt32[6]
    val cookie = PbOptional[PbBytes[15]]

    internal object Member : PbSchema() {
        val id = Id[1]
        val memberName = PbString[10]
        val specialTitle = PbOptional[PbString[17]]
        val memberCard = Card[11]
        val level = Level[12]
        val joinTimestamp = PbInt64[100]
        val lastMsgTimestamp = PbInt64[101]
        val shutUpTimestamp = PbOptional[PbInt64[102]]
        val permission = PbInt32[107]

        internal object Id : PbSchema() {
            val uid = PbString[2]
            val uin = PbInt64[4]
        }

        internal object Card : PbSchema() {
            val memberCard = PbOptional[PbString[2]]
        }

        internal object Level : PbSchema() {
            val infos = PbOptional[PbRepeatedInt32[1]]
            val level = PbInt32[2]
        }
    }
}