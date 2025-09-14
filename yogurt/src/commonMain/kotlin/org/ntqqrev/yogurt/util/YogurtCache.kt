package org.ntqqrev.yogurt.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.ntqqrev.acidify.common.struct.BotFriendCategoryData
import org.ntqqrev.acidify.common.struct.BotFriendData

typealias FriendCacheType = Pair<Map<Long, BotFriendData>, Map<Int, BotFriendCategoryData>>

class YogurtCache<T>(private val scope: CoroutineScope, private val load: suspend () -> T) {
    private val updateMutex = Mutex()
    private var currentTask: Deferred<T>? = null
    private var currentValue: T? = null

    suspend fun get(refresh: Boolean = false): T {
        if (!refresh) {
            currentValue?.let { return it }
        }
        return updateMutex.withLock {
            currentTask?.let {
                if (it.isActive) {
                    return@withLock it
                }
            }
            val newTask = scope.async {
                val data = load()
                currentValue = data
                data
            }
            currentTask = newTask
            newTask
        }.await()
    }
}