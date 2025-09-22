package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object FetchGroupsReq : PbSchema() {
    val config = Config[1]

    internal object Config : PbSchema() {
        val config1 = Config1[1]
        val config2 = Config2[2]
        val config3 = Config3[3]

        internal object Config1 : PbSchema() {
            val groupOwner = PbBoolean[1]
            val field2 = PbBoolean[2]
            val memberMax = PbBoolean[3]
            val memberCount = PbBoolean[4]
            val groupName = PbBoolean[5]
            val field8 = PbBoolean[8]
            val field9 = PbBoolean[9]
            val field10 = PbBoolean[10]
            val field11 = PbBoolean[11]
            val field12 = PbBoolean[12]
            val field13 = PbBoolean[13]
            val field14 = PbBoolean[14]
            val field15 = PbBoolean[15]
            val field16 = PbBoolean[16]
            val field17 = PbBoolean[17]
            val field18 = PbBoolean[18]
            val question = PbBoolean[19]
            val field20 = PbBoolean[20]
            val field22 = PbBoolean[22]
            val field23 = PbBoolean[23]
            val field24 = PbBoolean[24]
            val field25 = PbBoolean[25]
            val field26 = PbBoolean[26]
            val field27 = PbBoolean[27]
            val field28 = PbBoolean[28]
            val field29 = PbBoolean[29]
            val field30 = PbBoolean[30]
            val field31 = PbBoolean[31]
            val field32 = PbBoolean[32]
            val field5001 = PbBoolean[5001]
            val field5002 = PbBoolean[5002]
            val field5003 = PbBoolean[5003]
        }

        internal object Config2 : PbSchema() {
            val field1 = PbBoolean[1]
            val field2 = PbBoolean[2]
            val field3 = PbBoolean[3]
            val field4 = PbBoolean[4]
            val field5 = PbBoolean[5]
            val field6 = PbBoolean[6]
            val field7 = PbBoolean[7]
            val field8 = PbBoolean[8]
        }

        internal object Config3 : PbSchema() {
            val field5 = PbBoolean[5]
            val field6 = PbBoolean[6]
        }
    }
}

internal object FetchGroupsResp : PbSchema() {
    val groups = PbRepeated[Group[2]]

    internal object Group : PbSchema() {
        val groupUin = PbInt64[3]
        val info = Info[4]
        val customInfo = CustomInfo[5]

        internal object Info : PbSchema() {
            val groupOwner = Member[1]
            val createdTime = PbInt64[2]
            val memberMax = PbInt32[3]
            val memberCount = PbInt32[4]
            val groupName = PbString[5]
            val description = PbString[18]
            val question = PbString[19]
            val announcement = PbString[30]

            internal object Member : PbSchema() {
                val remark = PbString[3]
            }
        }

        internal object CustomInfo : PbSchema() {
            val lastSpeakTime = PbInt64[1]
            val lastestSeq = PbInt32[5]
        }
    }
}