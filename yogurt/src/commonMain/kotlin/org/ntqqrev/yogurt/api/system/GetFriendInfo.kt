package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetFriendInfoOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.api.invoke
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.FriendCacheType
import org.ntqqrev.yogurt.util.YogurtCache

val GetFriendInfo = ApiEndpoint.GetFriendInfo {
    val cache: YogurtCache<FriendCacheType> by application.dependencies
    val (f, fc) = cache.get(refresh = it.noCache)
    GetFriendInfoOutput(
        friend = f[it.userId]?.toMilkyEntity(fc)
            ?: throw MilkyApiException(-404, "Friend not found")
    )
}