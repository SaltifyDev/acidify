package org.ntqqrev.acidify.exception

class OidbException(
    val oidbCommand: Int,
    val oidbService: Int,
    val oidbResult: Int,
    val oidbErrorMsg: String
) : Exception("Oidb(cmd=${oidbCommand.toString(16)}, svc=${oidbService}) failed with $oidbResult: $oidbErrorMsg")