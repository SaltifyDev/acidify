@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package org.ntqqrev.yogurt.codec

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlin.experimental.ExperimentalNativeApi

actual fun audioToPcm(input: ByteArray) = processAudio(input, CodecLibrary.audioToPcm)

actual fun silkDecode(input: ByteArray) = processAudio(input, CodecLibrary.silkDecode)

actual fun silkEncode(input: ByteArray) = processAudio(input, CodecLibrary.silkEncode)

private fun processAudio(input: ByteArray, func: CPointer<AudioCodecFunction>): ByteArray = memScoped {
    val inputData = allocArray<ByteVar>(input.size)
    for (i in input.indices) {
        inputData[i] = input[i]
    }
    val inputDataRef = StableRef.create(inputData)
    val userData = Buffer()
    val userDataRef = StableRef.create(userData)
    val result = func.invoke(
        inputDataRef.get(),
        input.size,
        staticCFunction { userData, p, len ->
            val buffer = userData!!.asStableRef<Buffer>().get()
            val byteArray = p!!.readBytes(len)
            buffer.write(byteArray)
        },
        userDataRef.asCPointer()
    )
    require(result == 0) { "audio processing failed with code $result" }
    inputDataRef.dispose()
    userDataRef.dispose()
    return userData.readByteArray()
}