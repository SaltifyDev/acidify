package org.ntqqrev.acidify.message

/**
 * 构建发送消息
 */
interface BotOutgoingMessageBuilder {
    /**
     * 添加文本消息段
     * @param text 文本内容
     */
    fun text(text: String)

    /**
     * 添加提及（At）消息段
     * @param uin 被提及的用户的 uin（QQ 号），为 `null` 表示提及了所有人（`@全体成员`）
     */
    fun mention(uin: Long?)

    /**
     * 添加表情消息段
     * @param faceId 表情 ID
     * @param isLarge 是否为超级表情
     */
    fun face(faceId: Int, isLarge: Boolean = false)

    /**
     * 添加回复消息段
     * @param sequence 被回复的消息的序列号
     */
    fun reply(sequence: Long)

    /**
     * 添加图片消息段
     * @param raw 图片数据
     * @param format 图片格式
     * @param subType 图片子类型
     * @param summary 图片的文本描述
     */
    fun image(
        raw: ByteArray,
        format: ImageFormat,
        subType: ImageSubType = ImageSubType.NORMAL,
        summary: String = "[图片]"
    )

    /**
     * 添加语音消息段
     * @param raw 语音数据
     */
    fun record(raw: ByteArray)

    /**
     * 添加视频消息段
     * @param raw 视频数据
     * @param thumb 视频缩略图数据
     * @param thumbFormat 视频缩略图格式
     */
    fun video(raw: ByteArray, thumb: ByteArray, thumbFormat: ImageFormat)

    /**
     * 添加合并转发消息段
     * @param block 构建合并转发消息
     */
    fun forward(block: suspend Forward.() -> Unit)

    operator fun String.unaryPlus() = text(this)

    interface Forward {
        /**
         * 添加一个伪造合并转发消息
         * @param senderUin 该消息的发送者 uin（QQ 号）
         * @param senderName 该消息的发送者昵称
         * @param block 构建该消息的内容
         */
        fun node(senderUin: String, senderName: String, block: suspend BotOutgoingMessageBuilder.() -> Unit)
    }
}