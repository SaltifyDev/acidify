package org.ntqqrev.acidify.exception

class WtLoginException(
    code: Int,
    tag: String,
    message: String
) : Exception("WtLogin failed with code=$code ($tag: $message)")