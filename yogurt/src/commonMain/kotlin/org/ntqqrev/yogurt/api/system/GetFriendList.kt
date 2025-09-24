package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.dependencies
import io.ktor.server.routing.*
import org.ntqqrev.acidify.struct.BotFriendData
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetFriendListOutput
import org.ntqqrev.yogurt.api.invoke
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.YogurtCache

val GetFriendList = ApiEndpoint.GetFriendList {
    val friendCache = application.dependencies.resolve<YogurtCache<Long, BotFriendData>>()
    val friends = friendCache.getAll(cacheFirst = !it.noCache)
    GetFriendListOutput(
        friends = friends.map(BotFriendData::toMilkyEntity)
    )
}