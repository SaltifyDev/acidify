package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.yogurt.api.MilkyApi
import org.ntqqrev.yogurt.protocol.GetFriendListInput
import org.ntqqrev.yogurt.protocol.GetFriendListOutput
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.FriendCacheType
import org.ntqqrev.yogurt.util.YogurtCache

object GetFriendListApi : MilkyApi<GetFriendListInput, GetFriendListOutput>("get_friend_list") {
    override suspend fun Route.call(payload: GetFriendListInput): GetFriendListOutput {
        val cache: YogurtCache<FriendCacheType> by application.dependencies
        val (f, fc) = cache.get(refresh = payload.noCache)
        return GetFriendListOutput(
            friends = f.values.map { it.toMilkyEntity(fc) }
        )
    }
}