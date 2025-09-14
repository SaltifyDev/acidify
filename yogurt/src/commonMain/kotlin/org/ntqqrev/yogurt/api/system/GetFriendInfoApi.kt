package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.yogurt.api.MilkyApi
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.protocol.GetFriendInfoInput
import org.ntqqrev.yogurt.protocol.GetFriendInfoOutput
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.FriendCacheType
import org.ntqqrev.yogurt.util.YogurtCache

object GetFriendInfoApi : MilkyApi<GetFriendInfoInput, GetFriendInfoOutput>("get_friend_info") {
    override suspend fun Route.call(payload: GetFriendInfoInput): GetFriendInfoOutput {
        val cache: YogurtCache<FriendCacheType> by application.dependencies
        val (f, fc) = cache.get(refresh = payload.noCache)
        return GetFriendInfoOutput(
            friend = f[payload.userId]?.toMilkyEntity(fc)
                ?: throw MilkyApiException(-404, "Friend not found")
        )
    }
}