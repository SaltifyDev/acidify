package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object Oidb0x8FCReq : PbSchema() {
    val groupCode = PbInt64[1]
    val showFlag = PbInt32[2]
    val memLevelInfo = PbRepeated[MemberInfo[3]]
    val levelNames = PbRepeated[LevelName[4]]
    val updateTime = PbInt64[5]
    val officeMode = PbInt32[6]
    val groupOpenAppid = PbInt32[7]
    val client = ClientInfo[8]
    val authKey = PbBytes[9]

    internal object MemberInfo : PbSchema() {
        val uid = PbString[1]
        val point = PbInt32[2]
        val activeKey = PbInt32[3]
        val level = PbInt32[4]
        val specialTitle = PbBytes[5]
        val specialTitleExpireTime = PbInt32[6]
        val uinName = PbBytes[7]
        val memberCardName = PbBytes[8]
        val phone = PbBytes[9]
        val email = PbBytes[10]
        val remark = PbBytes[11]
    }

    internal object LevelName : PbSchema() {
        val level = PbInt32[1]
        val name = PbBytes[2]
    }

    internal object ClientInfo : PbSchema() {
        val implat = PbInt32[1]
        val clientVer = PbString[2]
    }
}