package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.acidify.common.struct.BotFriendData
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetFriendListOutput
import org.ntqqrev.yogurt.api.invoke
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.YogurtCache

val GetFriendList = ApiEndpoint.GetFriendList {
    val cache: YogurtCache<Long, BotFriendData> by application.dependencies
    val friends = cache.getAll(cacheFirst = !it.noCache)
    GetFriendListOutput(
        friends = friends.map(BotFriendData::toMilkyEntity)
    )
}