import org.ntqqrev.acidify.pb.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object TestSchema1 : PbSchema() {
    val intField = PbInt32[1]
    val longField = PbInt64[2]
    val repeatedIntField = PbRepeatedInt32[3]
    val repeatedLongField = PbRepeatedInt64[4]
    // Explicitly non-packed repeated field
    val notPackedRepeatedIntField = PbRepeatedInt32(5, encodePacked = false)
    val bytesField = PbBytes[6]
    val stringField = PbString[7]
    val repeatedStringField = PbRepeatedString[8]
    val nestedMessageField = TestSchema2[9]
    val repeatedNestedMessageField = PbRepeated[TestSchema2[10]]
    val optionalIntField = PbOptional[PbInt32[1]]
}

object TestSchema2 : PbSchema() {
    val intField = PbInt32[1]
    val longField = PbInt64[2]
    val bytesField = PbBytes[3]
    val stringField = PbString[4]
}

class PbTest {
    @Test
    fun `correctly encodes and decodes various field types`() {
        val message = TestSchema1 {
            it[intField] = 42
            it[longField] = 12345678901234L
            it[repeatedIntField] = listOf(1, 2, 3, 4, 5)
            it[repeatedLongField] = listOf(10000000000L, 20000000000L)
            it[notPackedRepeatedIntField] = listOf(10, 20, 30)
            it[bytesField] = byteArrayOf(0x01, 0x02, 0x03)
            it[stringField] = "Hello, World!"
            it[repeatedStringField] = listOf("foo", "bar", "baz")
            it[nestedMessageField] = TestSchema2 {
                it[intField] = 7
                it[longField] = 9876543210L
                it[bytesField] = byteArrayOf(0x0A, 0x0B, 0x0C)
                it[stringField] = "Nested"
            }
            it[repeatedNestedMessageField] = listOf(
                TestSchema2 {
                    it[intField] = 1
                    it[longField] = 111L
                    it[bytesField] = byteArrayOf(0x2A)
                    it[stringField] = "First"
                },
                TestSchema2 {
                    it[intField] = 2
                    it[longField] = 222L
                    it[bytesField] = byteArrayOf(0x2B)
                    it[stringField] = "Second"
                },
            )
            // optionalIntField is left unset to test default behavior
        }
        val encoded = message.toByteArray()
        println("Encoded size: ${encoded.size} bytes")
        println("Encoded data: ${encoded.toHexString()}")
        val decodedMessage = TestSchema1(encoded)
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
