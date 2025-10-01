package org.ntqqrev.yogurt.codec

import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import java.nio.file.Path
import java.util.*

class VideoInfoStruct : Structure() {
    @JvmField var width: Int = 0 // offset 0
    @JvmField var height: Int = 0 // offset 4
    @JvmField var duration: Long = 0 // offset 8

    override fun getFieldOrder(): MutableList<String?> {
        return mutableListOf<String?>("width", "height", "duration")
    }
}

fun interface AudioCodecCallback : Callback {
    fun invoke(userData: Pointer?, byteArray: Pointer?, length: Int)
}

fun interface AudioCodecFunction : Callback {
    operator fun invoke(data: Pointer?, length: Int, callback: AudioCodecCallback?, userData: Pointer?): Int
}

@Suppress("FunctionName")
interface CodecLibrary : Library {
    fun audio_to_pcm(data: Pointer?, length: Int, callback: AudioCodecCallback?, userData: Pointer?): Int

    fun silk_decode(data: Pointer?, length: Int, callback: AudioCodecCallback?, userData: Pointer?): Int
    fun silk_encode(data: Pointer?, length: Int, callback: AudioCodecCallback?, userData: Pointer?): Int

    fun video_first_frame(data: Pointer?, length: Int, outFrame: PointerByReference?, outSize: IntByReference?): Int
    fun video_get_size(data: Pointer?, length: Int, info: VideoInfoStruct?): Int
}

private val libraryPath: String
    get() {
        val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
        val arch = System.getProperty("os.arch").lowercase(Locale.getDefault())
        if (osName.contains("win")) {
            return if (arch.contains("64")) {
                "./lib/windows-x64/lagrangecodec"
            } else {
                "./lib/windows-x86/lagrangecodec"
            }
        } else if (osName.contains("mac")) {
            return if (arch.contains("aarch64") || arch.contains("arm64")) {
                "./lib/macos-arm64/liblagrangecodec.dylib"
            } else {
                "./lib/macos-x64/liblagrangecodec.dylib"
            }
        } else if (osName.contains("nux") || osName.contains("nix")) {
            if (arch.contains("aarch64") || arch.contains("arm64")) {
                return "./lib/linux-arm64/liblagrangecodec.so"
            } else if (arch.contains("x86_64") || arch.contains("amd64")) {
                return "./lib/linux-x64/liblagrangecodec.so"
            }
        }
        throw UnsupportedOperationException("Unsupported OS or architecture: $osName $arch")
    }

private val libraryAbsolutePath: String
    get() = Path.of(libraryPath).toAbsolutePath().toString()

val lib = Native.load(libraryAbsolutePath, CodecLibrary::class.java)!!