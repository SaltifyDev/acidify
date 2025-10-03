package org.ntqqrev.acidify.message

/**
 * 获取群精华消息结果
 * @property messages 精华消息列表
 * @property isEnd 是否已到达列表末尾
 */
class BotEssenceMessageResult(
    val messages: List<BotEssenceMessage>,
    val isEnd: Boolean
)