package org.ntqqrev.yogurt.transform

import org.ntqqrev.acidify.common.UserInfoGender
import org.ntqqrev.acidify.struct.BotFriendData
import org.ntqqrev.acidify.struct.BotGroupData
import org.ntqqrev.milky.FriendCategoryEntity
import org.ntqqrev.milky.FriendEntity
import org.ntqqrev.milky.GroupEntity

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

fun UserInfoGender.toMilkySex() = when (this) {
    UserInfoGender.MALE -> "male"
    UserInfoGender.FEMALE -> "female"
    else -> "unknown"
}