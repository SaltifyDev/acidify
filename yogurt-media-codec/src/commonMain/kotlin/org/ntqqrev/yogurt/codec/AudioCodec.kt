package org.ntqqrev.yogurt.codec

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

expect fun audioToPcm(input: ByteArray): ByteArray

expect fun silkDecode(input: ByteArray): ByteArray

expect fun silkEncode(input: ByteArray): ByteArray

// assume 16-bit PCM, 1 channel, 24kHz
fun calculatePcmDuration(input: ByteArray): Duration {
    val bitsPerSample = 16
    val channels = 1
    val sampleRate = 24000
    val bytesPerSecond = (bitsPerSample / 8) * channels * sampleRate
    return (input.size.toDouble() / bytesPerSecond).seconds
}