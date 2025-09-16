package org.ntqqrev.yogurt.transform

import org.ntqqrev.acidify.common.enum.UserInfoGender
import org.ntqqrev.acidify.common.struct.BotFriendData
import org.ntqqrev.milky.FriendCategoryEntity
import org.ntqqrev.milky.FriendEntity

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

fun UserInfoGender.toMilkySex() = when (this) {
    UserInfoGender.MALE -> "male"
    UserInfoGender.FEMALE -> "female"
    else -> "unknown"
}