package org.ntqqrev.acidify.exception

class ServiceException(
    cmd: String,
    retCode: Int,
    extra: String
) : Exception("Service ($cmd) call failed with code $retCode: $extra")