package org.ntqqrev.yogurt.api.system

import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.ntqqrev.milky.ApiEndpoint
import org.ntqqrev.milky.GetFriendInfoOutput
import org.ntqqrev.yogurt.api.MilkyApiException
import org.ntqqrev.yogurt.transform.toMilkyEntity
import org.ntqqrev.yogurt.util.FriendCache
import org.ntqqrev.yogurt.util.invoke

val GetFriendInfo = ApiEndpoint.GetFriendInfo {
    val friendCache = application.dependencies.resolve<FriendCache>()
    val friend = friendCache[it.userId, !it.noCache]
        ?: throw MilkyApiException(-404, "Friend not found")
    GetFriendInfoOutput(
        friend = friend.toMilkyEntity()
    )
}