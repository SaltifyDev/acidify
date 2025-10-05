package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.*

internal object GeneralGrayTip : PbSchema() {
    val bizType = PbInt32[1]
    val templateParams = PbRepeated[TemplateParam[7]]

    internal object TemplateParam : PbSchema() {
        val key = PbString[1]
        val value = PbString[2]
    }
}

