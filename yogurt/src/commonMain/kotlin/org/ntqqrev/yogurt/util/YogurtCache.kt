package org.ntqqrev.yogurt.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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