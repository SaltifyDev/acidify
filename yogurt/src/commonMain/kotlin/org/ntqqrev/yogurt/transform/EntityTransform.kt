package org.ntqqrev.yogurt.transform

import org.ntqqrev.acidify.common.enum.UserInfoGender
import org.ntqqrev.acidify.common.struct.BotFriendCategoryData
import org.ntqqrev.acidify.common.struct.BotFriendData
import org.ntqqrev.yogurt.protocol.FriendCategoryEntity
import org.ntqqrev.yogurt.protocol.FriendEntity

fun BotFriendData.toMilkyEntity(friendCacheMap: Map<Int, BotFriendCategoryData>) =
    FriendEntity(
        userId = uin,
        nickname = nickname,
        sex = gender.toMilkySex(),
        qid = qid,
        remark = remark,
        category = FriendCategoryEntity(
            categoryId = categoryId,
            categoryName = friendCacheMap[categoryId]?.name.orEmpty()
        )
    )

fun UserInfoGender.toMilkySex() = when (this) {
    UserInfoGender.MALE -> "male"
    UserInfoGender.FEMALE -> "female"
    else -> "unknown"
}