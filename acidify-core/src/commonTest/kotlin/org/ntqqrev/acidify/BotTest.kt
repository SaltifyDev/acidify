@file:OptIn(ExperimentalSerializationApi::class)

package org.ntqqrev.acidify

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.io.decodeFromSource
import kotlinx.serialization.json.io.encodeToSink
import org.ntqqrev.acidify.common.SessionStore
import org.ntqqrev.acidify.event.SessionStoreUpdatedEvent
import org.ntqqrev.acidify.util.log.LogLevel
import org.ntqqrev.acidify.util.log.SimpleColoredLogHandler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BotTest {
    private val session = if (SystemFileSystem.exists(sessionStorePath)) {
        SystemFileSystem.source(sessionStorePath).buffered().use {
            Json.decodeFromSource<SessionStore>(it)
        }
    } else {
        SessionStore.empty()
    }
    private val bot = Bot.create(
        appInfo = defaultSignProvider.getAppInfo()!!,
        sessionStore = session,
        signProvider = defaultSignProvider,
        scope = defaultScope,
        minLogLevel = LogLevel.VERBOSE,
        logHandler = SimpleColoredLogHandler,
    )

    init {
        defaultScope.launch {
            bot.eventFlow.collect {
                when (it) {
                    is SessionStoreUpdatedEvent -> {
                        SystemFileSystem.sink(sessionStorePath).buffered().use { sink ->
                            Json.encodeToSink(it.sessionStore, sink)
                        }
                    }
                }
            }
        }
        runBlocking {
            if (session.a2.isEmpty()) {
                bot.qrCodeLogin()
            } else {
                bot.tryLogin()
            }
        }
    }

    @Test
    fun logLevelTest() {
        val logger = bot.createLogger(this)
        logger.v { "Verbose (trace) message" }
        logger.d { "Debug message" }
        logger.i { "Info message" }
        logger.w { "Warning message" }
        logger.e { "Error message" }
    }

    @Test
    fun fetchFriendsTest() = runBlocking {
        val friends = bot.fetchFriends()
        assertTrue(friends.isNotEmpty())
        friends.forEach { println(it) }
    }

    @Test
    fun fetchGroupsTest() = runBlocking {
        val groups = bot.fetchGroups()
        assertTrue(groups.isNotEmpty())
        groups.forEach { println(it) }
    }

    @Test
    fun fetchGroupMembersTest() = runBlocking {
        val groups = bot.fetchGroups()
        assertTrue(groups.isNotEmpty())
        val group = groups.first()
        val members = bot.fetchGroupMembers(group.uin)
        assertTrue(members.isNotEmpty())
        members.forEach { println(it) }
    }

    @Test
    fun fetchUserInfoTest() = runBlocking {
        val friends = bot.fetchFriends()
        assertTrue(friends.isNotEmpty())
        val friend = friends.first()
        val userInfoByUin = bot.fetchUserInfoByUin(friend.uin)
        val userInfoByUid = bot.fetchUserInfoByUid(friend.uid)
        assertEquals(userInfoByUin, userInfoByUid)
        println(userInfoByUin)
    }

    @Test
    fun packetReceivingTest() = runBlocking {
        delay(30_000L)
    }
}