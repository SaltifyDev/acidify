@file:OptIn(ExperimentalForeignApi::class)

package org.ntqqrev.yogurt.codec

import kotlinx.cinterop.*

typealias ByteArrayPointer = CPointer<ByteVar>
typealias AudioCodecCallback = CPointer<CFunction<(COpaquePointer?, ByteArrayPointer?, Int) -> Unit>>
typealias AudioCodecFunction = CFunction<(ByteArrayPointer?, Int, AudioCodecCallback?, COpaquePointer?) -> Int>

class VideoInfoStruct(rawPtr: NativePtr) : CStructVar(rawPtr) {
    @Suppress("deprecation")
    companion object : Type(16, 8)

    var width: Int
        get() = memberAt<IntVar>(0).value
        set(value) {
            memberAt<IntVar>(0).value = value
        }

    var height: Int
        get() = memberAt<IntVar>(4).value
        set(value) {
            memberAt<IntVar>(4).value = value
        }

    var duration: Long
        get() = memberAt<LongVar>(8).value
        set(value) {
            memberAt<LongVar>(8).value = value
        }
}

object CodecLibrary {
    val audioToPcm: CPointer<AudioCodecFunction>
    val silkDecode: CPointer<AudioCodecFunction>
    val silkEncode: CPointer<AudioCodecFunction>

    val videoFirstFrame: CPointer<CFunction<(ByteArrayPointer?, Int, CPointerVarOf<ByteArrayPointer>?, CPointer<IntVar>?) -> Int>>
    val videoGetSize: CPointer<CFunction<(ByteArrayPointer?, Int, CPointer<VideoInfoStruct>?) -> Int>>

    init {
        val handle = loadCodecLibrary()
        audioToPcm = loadCodecFunction(handle, "audio_to_pcm").reinterpret()
        silkDecode = loadCodecFunction(handle, "silk_decode").reinterpret()
        silkEncode = loadCodecFunction(handle, "silk_encode").reinterpret()
        videoFirstFrame = loadCodecFunction(handle, "video_first_frame").reinterpret()
        videoGetSize = loadCodecFunction(handle, "video_get_size").reinterpret()
    }
}