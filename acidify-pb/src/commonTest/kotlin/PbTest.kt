import org.ntqqrev.acidify.pb.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object TestSchema1 : PbSchema() {
    val intField = pb int32 1
    val longField = pb int64 2
    val repeatedIntField = pb.repeated int32 3
    val repeatedLongField = pb.repeated int64 4
    // Explicitly non-packed repeated field
    val notPackedRepeatedIntField = PbRepeatedInt32(5, encodePacked = false)
    val bytesField = pb bytes 6
    val stringField = pb string 7
    val repeatedStringField = pb.repeated string 8
    val nestedMessageField = pb message TestSchema2 field 9
    val repeatedNestedMessageField = pb.repeated message TestSchema2 field 10
    val optionalIntField = pb.optional int32 11
}

object TestSchema2 : PbSchema() {
    val intField = pb int32 1
    val longField = pb int64 2
    val bytesField = pb bytes 3
    val stringField = pb string 4
}

class PbTest {
    @Test
    fun `correctly encodes and decodes various field types`() {
        val message = PbObject(TestSchema1) {
            set { intField to 42 }
            set { longField to 12345678901234L }
            set { repeatedIntField to listOf(1, 2, 3, 4, 5) }
            set { repeatedLongField to listOf(10000000000L, 20000000000L) }
            set { notPackedRepeatedIntField to listOf(10, 20, 30) }
            set { bytesField to byteArrayOf(0x01, 0x02, 0x03) }
            set { stringField to "Hello, World!" }
            set { repeatedStringField to listOf("foo", "bar", "baz") }
            set {
                nestedMessageField to PbObject(TestSchema2) {
                    set { intField to 7 }
                    set { longField to 9876543210L }
                    set { bytesField to byteArrayOf(0x0A, 0x0B, 0x0C) }
                    set { stringField to "Nested" }
                }
            }
            set {
                repeatedNestedMessageField to listOf(
                    PbObject(TestSchema2) {
                        set { intField to 1 }
                        set { longField to 111L }
                        set { bytesField to byteArrayOf(0x2A) }
                        set { stringField to "First" }
                    },
                    PbObject(TestSchema2) {
                        set { intField to 2 }
                        set { longField to 222L }
                        set { bytesField to byteArrayOf(0x2B) }
                        set { stringField to "Second" }
                    },
                )
            }
        }
        val encoded = message.toByteArray()
        println("Encoded size: ${encoded.size} bytes")
        println("Encoded data: ${encoded.toHexString()}")
        val decodedMessage = PbObject(TestSchema1, encoded)
        assertEquals(message.get { intField }, decodedMessage.get { intField })
        assertEquals(message.get { longField }, decodedMessage.get { longField })
        assertEquals(message.get { repeatedIntField }, decodedMessage.get { repeatedIntField })
        assertEquals(message.get { repeatedLongField }, decodedMessage.get { repeatedLongField })
        assertEquals(message.get { notPackedRepeatedIntField }, decodedMessage.get { notPackedRepeatedIntField })
        assertTrue(message.get { bytesField }.contentEquals(decodedMessage.get { bytesField }))
        assertEquals(message.get { stringField }, decodedMessage.get { stringField })
        assertEquals(message.get { repeatedStringField }, decodedMessage.get { repeatedStringField })
        val originalNested = message.get { nestedMessageField }
        val decodedNested = decodedMessage.get { nestedMessageField }
        assertEquals(originalNested.get { intField }, decodedNested.get { intField })
        assertEquals(originalNested.get { longField }, decodedNested.get { longField })
        assertTrue(originalNested.get { bytesField }.contentEquals(decodedNested.get { bytesField }))
        assertEquals(originalNested.get { stringField }, decodedNested.get { stringField })
        val originalRepeatedNested = message.get { repeatedNestedMessageField }
        val decodedRepeatedNested = decodedMessage.get { repeatedNestedMessageField }
        assertEquals(originalRepeatedNested.size, decodedRepeatedNested.size)
        for (i in originalRepeatedNested.indices) {
            val orig = originalRepeatedNested[i]
            val dec = decodedRepeatedNested[i]
            assertEquals(orig.get { intField }, dec.get { intField })
            assertEquals(orig.get { longField }, dec.get { longField })
            assertTrue(orig.get { bytesField }.contentEquals(dec.get { bytesField }))
            assertEquals(orig.get { stringField }, dec.get { stringField })
        }
        assertEquals(message.get { optionalIntField }, decodedMessage.get { optionalIntField })
    }
}
