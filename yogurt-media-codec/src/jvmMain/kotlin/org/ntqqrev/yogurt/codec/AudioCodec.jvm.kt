package org.ntqqrev.yogurt.codec

import io.github.kasukusakura.silkcodec.SilkCoder
import java.io.ByteArrayOutputStream
import javax.sound.sampled.*

fun getLeftChannel(bytes: ByteArray): ByteArray {
    val frameSize = 4 // 16-bit stereo PCM has 4 bytes per frame (2 bytes per channel)
    val leftChannel = ByteArray(bytes.size / 2)
    var j = 0
    for (i in bytes.indices step frameSize) {
        leftChannel[j++] = bytes[i]     // Left channel LSB
        leftChannel[j++] = bytes[i + 1] // Left channel MSB
    }
    return leftChannel
}

actual fun audioToMonoPcm(input: ByteArray): PcmWithSampleRate {
    val originalStream = AudioSystem.getAudioInputStream(input.inputStream())
    val targetFormat = AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED,
        originalStream.format.sampleRate,
        originalStream.format.sampleSizeInBits,
        originalStream.format.channels,
        originalStream.format.frameSize,
        originalStream.format.sampleRate,
        false,
    )
    val pcmStream = AudioSystem.getAudioInputStream(targetFormat, originalStream)
    val bytes = pcmStream.readBytes()
    originalStream.close()
    pcmStream.close()
    return PcmWithSampleRate(
        if (originalStream.format.channels == 2) getLeftChannel(bytes) else bytes,
        originalStream.format.sampleRate.toInt()
    )
}

actual fun silkDecodeToPcm24000(input: ByteArray): ByteArray {
    val memoryOutput = ByteArrayOutputStream()
    SilkCoder.decode(input.inputStream(), memoryOutput)
    return memoryOutput.toByteArray()
}

actual fun silkEncode(input: PcmWithSampleRate): ByteArray {
    val memoryOutput = ByteArrayOutputStream()
    SilkCoder.encode(input.data.inputStream(), memoryOutput, input.sampleRate)
    return memoryOutput.toByteArray()
}