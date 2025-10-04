package org.ntqqrev.yogurt.api.group

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.SetGroupAvatarOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.util.GroupCache
import org.ntqqrev.yogurt.util.invoke
import org.ntqqrev.yogurt.util.resolveUri

val SetGroupAvatar = ApiEndpoint.SetGroupAvatar {
    val bot = application.dependencies.resolve<Bot>()
    val groupCache = application.dependencies.resolve<GroupCache>()
    groupCache[it.groupId, true]
        ?: throw MilkyApiException(-404, "Group not found")

    // 解析图片 URI 并获取图片数据
    val imageData = resolveUri(it.imageUri)

    // 调用 Bot API 设置群头像
    bot.setGroupAvatar(it.groupId, imageData)

    SetGroupAvatarOutput()
}