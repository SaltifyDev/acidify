package org.ntqqrev.acidify.util

import co.touchlab.kermit.Logger
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter

internal fun createLogger(obj: Any): Logger = Logger(
    loggerConfigInit(platformLogWriter()),
    obj::class.qualifiedName ?: throw IllegalArgumentException("Cannot create logger for anonymous class")
)