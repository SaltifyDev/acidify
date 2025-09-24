package org.ntqqrev.yogurt.util

import co.touchlab.stately.collections.ConcurrentMutableMap
import io.ktor.server.application.*
import io.ktor.server.plugins.di.dependencies
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.struct.BotGroupData
import org.ntqqrev.acidify.struct.BotGroupMemberData
import org.ntqqrev.yogurt.YogurtApp.scope

fun Application.configureCacheDeps() {
    dependencies {
        provide {
            YogurtCache /* <Long, BotFriendData> */(scope) {
                val bot = this@configureCacheDeps.dependencies.resolve<Bot>()
                bot.fetchFriends().associateBy { it.uin }
            }
        }

        provide {
            YogurtCache /* <Long, BotGroupData> */(scope) {
                val bot = this@configureCacheDeps.dependencies.resolve<Bot>()
                val groupMap = bot.fetchGroups().associateBy { it.uin }
                // clean up group member caches that are not in group list
                val groupMemberMap =
                    this@configureCacheDeps.dependencies.resolve<ConcurrentMutableMap<Long, YogurtCache<Long, BotGroupMemberData>>>()
                groupMemberMap.apply {
                    (keys - groupMap.keys).forEach { remove(it) }
                }

                groupMap
            }
        }

        provide {
            ConcurrentMutableMap<Long, YogurtCache<Long, BotGroupMemberData>>()
        }
    }
}

suspend fun Application.resolveGroupMemberCache(groupUin: Long): YogurtCache<Long, BotGroupMemberData>? {
    val bot = dependencies.resolve<Bot>()
    val groupCache = dependencies.resolve<YogurtCache<Long, BotGroupData>>()
    val groupMemberMap = dependencies.resolve<ConcurrentMutableMap<Long, YogurtCache<Long, BotGroupMemberData>>>()
    groupCache[groupUin] ?: return null
    return groupMemberMap.getOrPut(groupUin) {
        YogurtCache(bot.scope) { bot.fetchGroupMembers(groupUin).associateBy { it.uin } }
    }
}