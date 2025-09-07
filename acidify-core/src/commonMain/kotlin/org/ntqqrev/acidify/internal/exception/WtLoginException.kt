package org.ntqqrev.acidify.internal.exception

class WtLoginException(
    code: Int,
    tag: String,
    message: String
) : Exception("WtLogin failed with code=$code ($tag: $message)")