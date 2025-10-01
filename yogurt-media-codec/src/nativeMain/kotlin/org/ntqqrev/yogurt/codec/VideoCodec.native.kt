@file:OptIn(ExperimentalForeignApi::class)

package org.ntqqrev.yogurt.codec

import korlibs.time.seconds
import kotlinx.cinterop.*

actual fun getVideoInfo(videoData: ByteArray): VideoInfo = memScoped {
    val inputData = allocArray<ByteVar>(videoData.size)
    for (i in videoData.indices) {
        inputData[i] = videoData[i]
    }
    val inputDataRef = StableRef.create(inputData)
    val infoStruct = alloc<VideoInfoStruct>()
    val result = CodecLibrary.videoGetSize.invoke(
        inputDataRef.get(),
        videoData.size,
        infoStruct.ptr
    )
    inputDataRef.dispose()
    require(result == 0) { "videoGetSize failed with code $result" }
    return VideoInfo(
        width = infoStruct.width,
        height = infoStruct.height,
        duration = infoStruct.duration.seconds
    )
}

actual fun getVideoFirstFrameJpg(videoData: ByteArray): ByteArray = memScoped {
    val inputData = allocArray<ByteVar>(videoData.size)
    for (i in videoData.indices) {
        inputData[i] = videoData[i]
    }
    val inputDataRef = StableRef.create(inputData)
    val outputDataPtr = alloc<CPointerVar<ByteVar>>()
    val outputLenPtr = alloc<IntVar>()
    val result = CodecLibrary.videoFirstFrame.invoke(
        inputDataRef.get(),
        videoData.size,
        outputDataPtr,
        outputLenPtr.ptr
    )
    inputDataRef.dispose()
    require(result == 0) { "videoFirstFrame failed with code $result" }
    val outputLen = outputLenPtr.value
    val outputData = outputDataPtr.value!!
    val byteArray = ByteArray(outputLen)
    for (i in 0 until outputLen) {
        byteArray[i] = outputData[i]
    }
    return byteArray
}