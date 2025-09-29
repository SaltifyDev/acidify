package org.ntqqrev.yogurt.util

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import org.ntqqrev.acidify.Bot

fun Application.configureCacheDeps() {
    dependencies {
        provide {
            FriendCache(this) {
                val bot = this@configureCacheDeps.dependencies.resolve<Bot>()
                bot.fetchFriends().associateBy { it.uin }
            }
        }

        provide {
            GroupCache(this) {
                val bot = this@configureCacheDeps.dependencies.resolve<Bot>()
                val groupMap = bot.fetchGroups().associateBy { it.uin }
                // clean up group member caches that are not in group list
                val groupMemberMap = this@configureCacheDeps.dependencies.resolve<GroupMemberMap>()
                groupMemberMap.apply {
                    (keys - groupMap.keys).forEach { remove(it) }
                }

                groupMap
            }
        }

        provide { GroupMemberMap() }
    }
}

suspend fun Application.resolveGroupMemberCache(groupUin: Long): GroupMemberCache? {
    val bot = dependencies.resolve<Bot>()
    val groupCache = dependencies.resolve<GroupCache>()
    val groupMemberMap = dependencies.resolve<GroupMemberMap>()
    groupCache[groupUin] ?: return null
    return groupMemberMap.getOrPut(groupUin) {
        GroupMemberCache(bot.scope) { bot.fetchGroupMembers(groupUin).associateBy { it.uin } }
    }
}