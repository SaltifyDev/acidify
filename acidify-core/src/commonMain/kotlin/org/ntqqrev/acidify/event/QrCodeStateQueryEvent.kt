package org.ntqqrev.acidify.event

import org.ntqqrev.acidify.common.QrCodeState

/**
 * 二维码状态查询事件
 * @property state 二维码状态
 */
class QrCodeStateQueryEvent(val state: QrCodeState) : AcidifyEvent