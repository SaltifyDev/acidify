package org.ntqqrev.acidify.internal.exception

class ServiceException(
    cmd: String,
    retCode: Int,
    extra: String
) : Exception("Service ($cmd) call failed with code $retCode: $extra")