import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import org.ntqqrev.yogurt.codec.audioToPcm
import org.ntqqrev.yogurt.codec.getVideoFirstFrameJpg
import org.ntqqrev.yogurt.codec.getImageInfo
import org.ntqqrev.yogurt.codec.getVideoInfo
import kotlin.test.Test
import kotlin.test.assertEquals

class CodecTest {
    val testOutputPath = Path("test-output").also {
        if (!SystemFileSystem.exists(it)) {
            SystemFileSystem.createDirectories(it)
        }
    }

    @Test
    fun imageDecodeTest() {
        listOf(
            "png",
            "jpg",
            "gif",
            "webp",
            "bmp",
            "tiff"
        ).forEach {
            val file = SystemFileSystem.source(Path("src/commonTest/resources/image/test.$it"))
                .buffered()
                .readByteArray()
            val info = getImageInfo(file)
            assertEquals(info.format.ext, it)
            println("Image info of test.$it: $info")
        }
    }

    @Test
    fun audioToPcmTest() {
        val file = SystemFileSystem.source(Path("src/commonTest/resources/audio/test.mp3"))
            .buffered()
            .readByteArray()
        val pcm = audioToPcm(file)
        println("PCM size: ${pcm.size}")
        SystemFileSystem.sink(Path("test-output/test-pcm-24000.pcm")).buffered().use {
            it.write(pcm)
        }
    }

    @Test
    fun videoGetInfoTest() {
        val file = SystemFileSystem.source(Path("src/commonTest/resources/video/test.mp4"))
            .buffered()
            .readByteArray()
        val info = getVideoInfo(file)
        println("Video info: $info")
    }

    @Test
    fun videoGetFirstFrameTest() {
        val file = SystemFileSystem.source(Path("src/commonTest/resources/video/test.mp4"))
            .buffered()
            .readByteArray()
        val jpg = getVideoFirstFrameJpg(file)
        SystemFileSystem.sink(Path("test-output/test-video-first-frame.jpg")).buffered().use {
            it.write(jpg)
        }
    }
}