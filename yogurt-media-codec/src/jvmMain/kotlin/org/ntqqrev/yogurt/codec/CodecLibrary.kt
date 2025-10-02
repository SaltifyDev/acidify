package org.ntqqrev.yogurt.codec

import com.sun.jna.*
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files
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

/**
 * 获取资源路径中的库文件路径
 */
private fun getLibraryResourcePath(): String {
    val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
    val arch = System.getProperty("os.arch").lowercase(Locale.getDefault())

    val (platform, fileName) = when {
        osName.contains("win") -> {
            val platform = if (arch.contains("64")) "windows-x64" else "windows-x86"
            platform to "lagrangecodec.dll"
        }

        osName.contains("mac") -> {
            val platform = if (arch.contains("aarch64") || arch.contains("arm64")) {
                "macos-arm64"
            } else {
                "macos-x64"
            }
            platform to "liblagrangecodec.dylib"
        }

        osName.contains("nux") || osName.contains("nix") -> {
            val platform = when {
                arch.contains("aarch64") || arch.contains("arm64") -> "linux-arm64"
                arch.contains("x86_64") || arch.contains("amd64") -> "linux-x64"
                else -> throw UnsupportedOperationException("Unsupported architecture: $arch")
            }
            platform to "liblagrangecodec.so"
        }

        else -> throw UnsupportedOperationException("Unsupported OS: $osName")
    }

    return "/lib/$platform/$fileName"
}

/**
 * 从资源中提取库文件到临时目录
 */
private fun extractLibraryToTemp(): String {
    val resourcePath = getLibraryResourcePath()
    val inputStream: InputStream = CodecLibrary::class.java.getResourceAsStream(resourcePath)
        ?: throw IllegalStateException("无法找到库文件: $resourcePath")

    // 创建临时目录
    val tempDir = Files.createTempDirectory("yogurt-codec-").toFile()
    tempDir.deleteOnExit()

    // 获取库文件名
    val libraryFileName = resourcePath.substringAfterLast('/')
    val tempLibFile = File(tempDir, libraryFileName)
    tempLibFile.deleteOnExit()

    // 将库文件从资源复制到临时目录
    inputStream.use { input ->
        FileOutputStream(tempLibFile).use { output ->
            input.copyTo(output)
        }
    }

    // 确保文件可执行（对于 Unix 系统）
    if (!System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win")) {
        tempLibFile.setExecutable(true, false)
        tempLibFile.setReadable(true, false)
        tempLibFile.setWritable(true, true)
    }

    return tempLibFile.absolutePath
}

private val libraryAbsolutePath: String by lazy {
    extractLibraryToTemp()
}

val lib = Native.load(libraryAbsolutePath, CodecLibrary::class.java)!!