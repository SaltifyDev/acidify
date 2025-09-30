package org.ntqqrev.yogurt.codec

class PcmWithSampleRate(val data: ByteArray, val sampleRate: Int)

expect fun audioToMonoPcm(input: ByteArray): PcmWithSampleRate

expect fun silkDecodeToPcm24000(input: ByteArray): ByteArray

expect fun silkEncode(input: PcmWithSampleRate): ByteArray