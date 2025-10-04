package org.ntqqrev.acidify.event

/**
 * 好友文件上传事件
 * @property userUin 好友 QQ 号
 * @property userUid 好友 uid
 * @property fileId 文件 ID
 * @property fileName 文件名称
 * @property fileSize 文件大小（字节）
 * @property fileHash 文件的 TriSHA1 哈希值
 * @property isSelf 是否是自己发送的文件
 */
class FriendFileUploadEvent(
    val userUin: Long,
    val userUid: String,
    val fileId: String,
    val fileName: String,
    val fileSize: Long,
    val fileHash: String,
    val isSelf: Boolean
) : AcidifyEvent