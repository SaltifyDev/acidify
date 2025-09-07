package org.ntqqrev.acidify

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.ntqqrev.acidify.util.UrlSignProvider

val defaultSignProvider = UrlSignProvider("https://sign.lagrangecore.org/api/sign/39038")
val defaultScope = CoroutineScope(Dispatchers.IO)
val sessionStorePath = Path("acidify-core-test-data", "session.json").also {
    SystemFileSystem.createDirectories(it.parent!!)
}