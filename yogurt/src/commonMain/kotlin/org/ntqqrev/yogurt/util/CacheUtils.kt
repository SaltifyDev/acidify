package org.ntqqrev.yogurt.util

import co.touchlab.stately.collections.ConcurrentMutableMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.ntqqrev.acidify.struct.BotFriendData
import org.ntqqrev.acidify.struct.BotGroupData
import org.ntqqrev.acidify.struct.BotGroupMemberData

class YogurtCache<K, V>(
    val scope: CoroutineScope,
    val fetchData: suspend () -> Map<K, V>
) {
    private val updateMutex = Mutex()
    private var currentTask: Deferred<Unit>? = null

    private var currentCache = mutableMapOf<K, V>()

    suspend operator fun get(key: K, cacheFirst: Boolean = true): V? {
        if (key !in currentCache || !cacheFirst) {
            update()
        }
        return currentCache[key]
    }

    suspend fun getAll(cacheFirst: Boolean = true): Iterable<V> {
        if (currentCache.isEmpty() || !cacheFirst) {
            update()
        }
        return currentCache.values
    }

    suspend operator fun set(key: K, value: V) {
        return updateMutex.withLock {
            currentCache[key] = value
        }
    }

    suspend fun update() {
        return updateMutex.withLock {
            currentTask?.let {
                if (it.isActive) {
                    return@withLock it
                }
            }
            val newTask = scope.async {
                val data = fetchData()
                currentCache = data.toMutableMap()
            }
            currentTask = newTask
            newTask
        }.await()
    }
}

typealias FriendCache = YogurtCache<Long, BotFriendData>
typealias GroupCache = YogurtCache<Long, BotGroupData>
typealias GroupMemberCache = YogurtCache<Long, BotGroupMemberData>
typealias GroupMemberMap = ConcurrentMutableMap<Long, GroupMemberCache>