package org.ntqqrev.acidify.common.log

class LogMessage(
    val level: LogLevel,
    val tag: String,
    val messageSupplier: () -> String,
    val throwable: Throwable? = null,
) {
    init {
        require(throwable == null || level >= LogLevel.WARN) {
            "Throwable should only be provided for WARN or ERROR level logs"
        }
    }
}