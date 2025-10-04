package org.ntqqrev.yogurt.transform

import org.ntqqrev.acidify.struct.*
import org.ntqqrev.milky.*

fun BotFriendData.toMilkyEntity() =
    FriendEntity(
        userId = uin,
        nickname = nickname,
        sex = gender.toMilkyString(),
        qid = qid,
        remark = remark,
        category = FriendCategoryEntity(
            categoryId = categoryId,
            categoryName = categoryName
        )
    )

fun BotGroupData.toMilkyEntity() =
    GroupEntity(
        groupId = uin,
        groupName = name,
        memberCount = memberCount,
        maxMemberCount = capacity
    )

fun BotGroupMemberData.toMilkyEntity(withGroupUin: Long) =
    GroupMemberEntity(
        userId = uin,
        nickname = nickname,
        sex = "unknown",
        groupId = withGroupUin,
        card = card,
        title = specialTitle,
        level = level,
        role = role.toMilkyString(),
        joinTime = joinedAt,
        lastSentTime = lastSpokeAt,
        shutUpEndTime = mutedUntil
    )

fun BotUserInfo.toMilkyOutput() =
    GetUserProfileOutput(
        nickname = nickname,
        qid = qid,
        age = age,
        sex = gender.toMilkyString(),
        remark = remark,
        bio = bio,
        level = level,
        country = country,
        city = city,
        school = school
    )

fun UserInfoGender.toMilkyString() = when (this) {
    UserInfoGender.MALE -> "male"
    UserInfoGender.FEMALE -> "female"
    else -> "unknown"
}

fun GroupMemberRole.toMilkyString() = when (this) {
    GroupMemberRole.OWNER -> "owner"
    GroupMemberRole.ADMIN -> "admin"
    GroupMemberRole.MEMBER -> "member"
}

fun BotGroupFileEntry.toMilkyEntity(groupId: Long) =
    GroupFileEntity(
        groupId = groupId,
        fileId = fileId,
        fileName = fileName,
        parentFolderId = parentFolderId,
        fileSize = fileSize,
        uploadedTime = uploadedTime,
        expireTime = expireTime,
        uploaderId = uploaderUin,
        downloadedTimes = downloadedTimes
    )

fun BotGroupFolderEntry.toMilkyEntity(groupId: Long) =
    GroupFolderEntity(
        groupId = groupId,
        folderId = folderId,
        parentFolderId = parentFolderId,
        folderName = folderName,
        createdTime = createTime,
        lastModifiedTime = modifiedTime,
        creatorId = creatorUin,
        fileCount = totalFileCount
    )