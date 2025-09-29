package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object FetchFaceDetailsReq : PbSchema() {
    val field1 = PbInt32[1]
    val field2 = PbInt32[2]
    val field3 = PbString[3]
}

internal object FetchFaceDetailsResp : PbSchema() {
    val field1 = PbInt32[1]
    val commonFace = ResponseContent[2]
    val specialBigFace = ResponseContent[3]
    val specialMagicFace = MagicEmojiContent[4]

    internal object ResponseContent : PbSchema() {
        val emojiList = PbRepeated[EmojiList[1]]
        val resourceUrl = ResourceUrl[2]
    }

    internal object MagicEmojiContent : PbSchema() {
        val field1 = MagicEmojiList[1]
        val resourceUrl = ResourceUrl[2]
    }

    internal object MagicEmojiList : PbSchema() {
        val emojiList = PbRepeated[Emoji[2]]
    }

    internal object EmojiList : PbSchema() {
        val emojiPackName = PbString[1]
        val emojiDetail = PbRepeated[Emoji[2]]
    }

    internal object Emoji : PbSchema() {
        val qSid = PbString[1]
        val qDes = PbString[2]
        val emCode = PbString[3]
        val qCid = PbInt32[4]
        val aniStickerType = PbInt32[5]
        val aniStickerPackId = PbInt32[6]
        val aniStickerId = PbInt32[7]
        val url = ResourceUrl[8]
        val emojiNameAlias = PbRepeatedString[9]
        val unknown10 = PbInt32[10]
        val aniStickerWidth = PbInt32[13]
        val aniStickerHeight = PbInt32[14]
    }

    internal object ResourceUrl : PbSchema() {
        val baseUrl = PbString[1]
        val advUrl = PbString[2]
    }
}