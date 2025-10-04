package org.ntqqrev.acidify.event

/**
 * 机器人离线事件
 * @property reason 下线原因
 */
class BotOfflineEvent(
    val reason: String
) : AcidifyEvent

