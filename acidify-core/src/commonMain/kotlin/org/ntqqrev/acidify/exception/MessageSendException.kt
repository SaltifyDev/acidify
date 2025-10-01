package org.ntqqrev.acidify.exception

class MessageSendException(
    val resultCode: Int,
    val errorMessage: String
) : Exception("Message sending failed with code $resultCode: $errorMessage")