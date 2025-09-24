package org.ntqqrev.yogurt.transform

import org.ntqqrev.acidify.common.GroupMemberRole
import org.ntqqrev.acidify.common.UserInfoGender
import org.ntqqrev.acidify.struct.BotFriendData
import org.ntqqrev.acidify.struct.BotGroupData
import org.ntqqrev.acidify.struct.BotGroupMemberData
import org.ntqqrev.acidify.struct.BotUserInfo
import org.ntqqrev.milky.FriendCategoryEntity
import org.ntqqrev.milky.FriendEntity
import org.ntqqrev.milky.GetUserProfileOutput
import org.ntqqrev.milky.GroupEntity
import org.ntqqrev.milky.GroupMemberEntity

fun BotFriendData.toMilkyEntity() =
    FriendEntity(
        userId = uin,
        nickname = nickname,
        sex = gender.toMilkySex(),
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
        role = role.toMilkyRole(),
        joinTime = joinedAt,
        lastSentTime = lastSpokeAt,
        shutUpEndTime = mutedUntil
    )

fun BotUserInfo.toMilkyOutput() =
    GetUserProfileOutput(
        nickname = nickname,
        qid = qid,
        age = age,
        sex = gender.toMilkySex(),
        remark = remark,
        bio = bio,
        level = level,
        country = country,
        city = city,
        school = school
    )

fun UserInfoGender.toMilkySex() = when (this) {
    UserInfoGender.MALE -> "male"
    UserInfoGender.FEMALE -> "female"
    else -> "unknown"
}

fun GroupMemberRole.toMilkyRole() = when (this) {
    GroupMemberRole.OWNER -> "owner"
    GroupMemberRole.ADMIN -> "admin"
    GroupMemberRole.MEMBER -> "member"
}