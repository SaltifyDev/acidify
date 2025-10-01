package org.ntqqrev.yogurt.codec

import com.sun.jna.Memory
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import kotlin.time.Duration.Companion.seconds

actual fun getVideoInfo(videoData: ByteArray): VideoInfo {
    val lib = lib
    val inputMem = Memory(videoData.size.toLong())
    inputMem.write(0, videoData, 0, videoData.size)
    val infoStruct = VideoInfoStruct()
    val result = lib.video_get_size(inputMem, videoData.size, infoStruct)
    require(result == 0) { "videoGetSize failed with code $result" }
    inputMem.clear()
    return VideoInfo(
        width = infoStruct.width,
        height = infoStruct.height,
        duration = infoStruct.duration.seconds
    )
}

actual fun getVideoFirstFrameJpg(videoData: ByteArray): ByteArray {
    val lib = lib
    val inputMem = Memory(videoData.size.toLong())
    inputMem.write(0, videoData, 0, videoData.size)
    val outputPtr = PointerByReference()
    val outputLenPtr = IntByReference()
    val result = lib.video_first_frame(inputMem, videoData.size, outputPtr, outputLenPtr)
    require(result == 0) { "videoFirstFrame failed with code $result" }
    val outputLen = outputLenPtr.value
    val outputMem = outputPtr.value
    val byteArray = ByteArray(outputLen)
    outputMem.read(0, byteArray, 0, outputLen)
    inputMem.clear()
    return byteArray
}