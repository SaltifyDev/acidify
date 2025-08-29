package org.ntqqrev.acidify.internal

import kotlinx.coroutines.CoroutineScope
import org.ntqqrev.acidify.common.AppInfo
import org.ntqqrev.acidify.common.SessionStore
import org.ntqqrev.acidify.common.SignProvider
import org.ntqqrev.acidify.internal.logic.LoginLogic
import org.ntqqrev.acidify.internal.logic.PacketLogic

internal class Client(
    val appInfo: AppInfo,
    val sessionStore: SessionStore,
    val signProvider: SignProvider,
    val scope: CoroutineScope
) {
    val loginLogic = LoginLogic(this)
    val packetLogic = PacketLogic(this)
}