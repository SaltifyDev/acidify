package org.ntqqrev.acidify.event

import org.ntqqrev.acidify.message.BotIncomingMessage

/**
 * 消息接收事件
 * @property message 接收到的消息
 */
class MessageReceiveEvent(val message: BotIncomingMessage) : AcidifyEvent