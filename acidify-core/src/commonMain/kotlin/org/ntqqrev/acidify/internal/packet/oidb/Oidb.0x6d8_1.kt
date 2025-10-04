package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object GroupFileListReq : PbSchema() {
    val listReq = GroupFileListReqBody[2]
}

internal object GroupFileListReqBody : PbSchema() {
    val groupUin = PbInt64[1]
    val appId = PbInt32[2]
    val targetDirectory = PbString[3]
    val fileCount = PbInt32[5]
    val sortBy = PbInt32[9]
    val startIndex = PbInt32[13]
    val field17 = PbInt32[17]
    val field18 = PbInt32[18]
}

internal object GroupFileListResp : PbSchema() {
    val listResp = GroupFileListRespBody[2]
}

internal object GroupFileListRespBody : PbSchema() {
    val retCode = PbInt32[1]
    val isEnd = PbBoolean[4]
    val items = PbRepeated[GroupFileListItem[5]]
}

internal object GroupFileListItem : PbSchema() {
    val type = PbInt32[1]
    val folderInfo = GroupFolderInfo[2]
    val fileInfo = GroupFileInfo[3]
}

internal object GroupFileInfo : PbSchema() {
    val fileId = PbString[1]
    val fileName = PbString[2]
    val fileSize = PbInt64[3]
    val busId = PbInt32[4]
    val uploadedSize = PbInt64[5]
    val uploadedTime = PbInt64[6]
    val expireTime = PbInt64[7]
    val modifiedTime = PbInt64[8]
    val downloadedTimes = PbInt32[9]
    val fileSha1 = PbBytes[10]
    val fileMd5 = PbBytes[12]
    val uploaderName = PbString[14]
    val uploaderUin = PbInt64[15]
    val parentDirectory = PbString[16]
    val field17 = PbInt32[17]
    val field22 = PbString[22]
}

internal object GroupFolderInfo : PbSchema() {
    val folderId = PbString[1]
    val parentDirectoryId = PbString[2]
    val folderName = PbString[3]
    val createTime = PbInt64[4]
    val modifiedTime = PbInt64[5]
    val creatorUin = PbInt64[6]
    val creatorName = PbString[7]
    val totalFileCount = PbInt32[8]
}