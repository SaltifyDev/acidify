import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import org.ntqqrev.yogurt.codec.getImageInfo
import kotlin.test.Test
import kotlin.test.assertEquals

class CodecTest {
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
}