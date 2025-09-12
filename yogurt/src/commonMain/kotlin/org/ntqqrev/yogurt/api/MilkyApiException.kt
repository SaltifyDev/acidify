package org.ntqqrev.yogurt.api

class MilkyApiException(
    val retcode: Int,
    override val message: String?,
) : Exception()