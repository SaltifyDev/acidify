package org.ntqqrev.yogurt.codec

expect fun audioToPcm(input: ByteArray): ByteArray

expect fun silkDecode(input: ByteArray): ByteArray

expect fun silkEncode(input: ByteArray): ByteArray