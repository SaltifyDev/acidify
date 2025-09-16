package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetFriendListOutput
import org.ntqqrev.yogurt.api.invoke
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.FriendCacheType
import org.ntqqrev.yogurt.util.YogurtCache

val GetFriendList = ApiEndpoint.GetFriendList {
    val cache: YogurtCache<FriendCacheType> by application.dependencies
    val (f, fc) = cache.get(refresh = it.noCache)
    GetFriendListOutput(
        friends = f.values.map { it.toMilkyEntity(fc) }
    )
}