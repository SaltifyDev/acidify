package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object FetchPsKeyReq : PbSchema() {
    val domains = PbRepeatedString[1]
}

internal object FetchPsKeyResp : PbSchema() {
    val psKeyEntries = PbRepeated[PsKeyEntry[1]]

    internal object PsKeyEntry : PbSchema() {
        val domain = PbString[1]
        val key = PbString[2]
    }
}