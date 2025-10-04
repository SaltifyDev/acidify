package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object Oidb0x6D7Req : PbSchema() {
    val createFolder = CreateFolder[1]
    val deleteFolder = DeleteFolder[2]
    val renameFolder = RenameFolder[3]

    internal object CreateFolder : PbSchema() {
        val groupUin = PbInt64[1]
        val rootDirectory = PbString[3]
        val folderName = PbString[4]
    }

    internal object DeleteFolder : PbSchema() {
        val groupUin = PbInt64[1]
        val folderId = PbString[3]
    }

    internal object RenameFolder : PbSchema() {
        val groupUin = PbInt64[1]
        val folderId = PbString[3]
        val newFolderName = PbString[4]
    }
}

internal object Oidb0x6D7Resp : PbSchema() {
    val createFolder = CreateFolderResp[1]
    val deleteFolder = SimpleResp[2]
    val renameFolder = SimpleResp[3]

    internal object CreateFolderResp : PbSchema() {
        val retCode = PbInt32[1]
        val retMsg = PbString[2]
        val folderId = PbString[3]
    }

    internal object SimpleResp : PbSchema() {
        val retCode = PbInt32[1]
        val retMsg = PbString[2]
    }
}
