package org.ntqqrev.acidify.internal.packet.highway

import org.ntqqrev.acidify.pb.*

internal object NTV2RichMediaHighwayExt : PbSchema() {
    val fileUuid = PbString[1]
    val uKey = PbString[2]
    val network = NTHighwayNetwork[5]
    val msgInfoBody = PbRepeated[MsgInfoBody[6]]
    val blockSize = PbInt32[10]
    val hash = NTHighwayHash[11]
}

internal object NTHighwayHash : PbSchema() {
    val fileSha1 = PbRepeatedBytes[1]
}

internal object NTHighwayNetwork : PbSchema() {
    val iPv4s = PbRepeated[NTHighwayIPv4[1]]
}

internal object NTHighwayIPv4 : PbSchema() {
    val domain = NTHighwayDomain[1]
    val port = PbInt32[2]
}

internal object NTHighwayDomain : PbSchema() {
    val isEnable = PbBoolean[1]
    val iP = PbString[2]
}