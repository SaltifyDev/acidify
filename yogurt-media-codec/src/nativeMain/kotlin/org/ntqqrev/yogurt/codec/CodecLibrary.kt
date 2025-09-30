@file:OptIn(ExperimentalForeignApi::class)

package org.ntqqrev.yogurt.codec

import kotlinx.cinterop.*

typealias ByteArrayPointer = CPointer<ByteVar>
typealias AudioCodecCallback = CPointer<CFunction<(COpaquePointer?, ByteArrayPointer?, Int) -> Unit>>
typealias AudioCodecFunction = CFunction<(ByteArrayPointer?, Int, AudioCodecCallback?, COpaquePointer?) -> Int>

object CodecLibrary {
    val audioToPcm: CPointer<AudioCodecFunction>
    val silkDecode: CPointer<AudioCodecFunction>
    val silkEncode: CPointer<AudioCodecFunction>

    init {
        val handle = loadCodecLibrary()
        audioToPcm = loadCodecFunction(handle, "audio_to_pcm").reinterpret()
        silkDecode = loadCodecFunction(handle, "silk_decode").reinterpret()
        silkEncode = loadCodecFunction(handle, "silk_encode").reinterpret()
        println("loaded codec library functions")
    }
}