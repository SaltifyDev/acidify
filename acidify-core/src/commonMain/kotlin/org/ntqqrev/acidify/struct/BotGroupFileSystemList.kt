package org.ntqqrev.acidify.struct

/**
 * 群文件系统列表结果
 *
 * @property files 文件列表
 * @property folders 文件夹列表
 */
data class BotGroupFileSystemList(
    val files: List<BotGroupFileEntry>,
    val folders: List<BotGroupFolderEntry>
)