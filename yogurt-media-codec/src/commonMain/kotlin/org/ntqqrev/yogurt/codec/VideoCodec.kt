package org.ntqqrev.yogurt.codec

import kotlin.time.Duration

data class VideoInfo(
    val width: Int,
    val height: Int,
    val duration: Duration,
)

expect fun getVideoInfo(videoData: ByteArray): VideoInfo

expect fun getVideoFirstFrameJpg(videoData: ByteArray): ByteArray