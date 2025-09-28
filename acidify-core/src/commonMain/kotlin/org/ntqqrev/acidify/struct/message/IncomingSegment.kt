package org.ntqqrev.acidify.struct.message

/**
 * 接收消息段
 */
sealed class IncomingSegment {
    /**
     * 文本消息段
     * @property text 文本内容
     */
    class Text internal constructor(
        val text: String,
    ) : IncomingSegment()

    /**
     * 提及（At）消息段
     * @property uin 被提及的用户的 uin（QQ 号），为 `null` 表示提及了所有人（`@全体成员`）
     * @property name 被提及的用户的名称，视情况有可能是昵称 / 备注 / 群名片 / `@全体成员`
     */
    class Mention internal constructor(
        val uin: Long? = null,
        val name: String,
    ) : IncomingSegment()

    /**
     * 表情消息段
     * @property faceId 表情 ID
     * @property summary 表情的文本描述
     * @property isLarge 是否为超级表情
     */
    class Face internal constructor(
        val faceId: Int,
        val summary: String,
        val isLarge: Boolean,
    ) : IncomingSegment()

    /**
     * 回复消息段
     * @property sequence 被回复的消息的序列号
     */
    class Reply internal constructor(
        val sequence: Long,
    ) : IncomingSegment()

    /**
     * 图片消息段
     * @property fileId 图片的文件 ID
     * @property width 图片的宽度
     * @property height 图片的高度
     * @property subType 图片子类型
     * @property summary 图片的文本描述
     */
    class Image internal constructor(
        val fileId: String,
        val width: Int,
        val height: Int,
        val subType: ImageSubType,
        val summary: String,
    ) : IncomingSegment()

    /**
     * 语音消息段
     * @property fileId 语音的文件 ID
     * @property duration 语音的时长（秒）
     */
    class Record internal constructor(
        val fileId: String,
        val duration: Int,
    ) : IncomingSegment()

    /**
     * 视频消息段
     * @property fileId 视频的文件 ID
     * @property duration 视频的时长（秒）
     * @property width 视频的宽度
     * @property height 视频的高度
     */
    class Video internal constructor(
        val fileId: String,
        val duration: Int,
        val width: Int,
        val height: Int,
    ) : IncomingSegment()

    /**
     * 文件消息段
     * @property fileId 文件 ID
     * @property fileName 文件名称
     * @property fileSize 文件大小（字节）
     * @property fileHash 文件的 TriSHA1 哈希值，仅在私聊文件中存在
     */
    class File internal constructor(
        val fileId: String,
        val fileName: String,
        val fileSize: Long,
        val fileHash: String? = null,
    ) : IncomingSegment()

    /**
     * 转发消息段
     * @property resId 转发消息的资源 ID
     */
    class Forward internal constructor(
        val resId: String,
    ) : IncomingSegment()

    /**
     * 小程序消息段
     * @property appName 小程序 App Name
     * @property jsonPayload 小程序的 JSON 负载
     */
    class LightApp internal constructor(
        val appName: String,
        val jsonPayload: String,
    ) : IncomingSegment()
}