package org.ntqqrev.acidify.exception

/**
 * Bot 上线异常
 * @property systemMsg 提示异常的系统消息
 */
class BotOnlineException(
    val systemMsg: String
) : Exception("Bot online failed: $systemMsg")