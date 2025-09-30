import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import org.ntqqrev.yogurt.codec.audioToMonoPcm
import org.ntqqrev.yogurt.codec.getImageInfo
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
        val pcm = audioToMonoPcm(file).data
        println("PCM size: ${pcm.size}")
        SystemFileSystem.sink(Path("test-output/test-pcm-24000.pcm")).buffered().use {
            it.write(pcm)
        }
    }
}