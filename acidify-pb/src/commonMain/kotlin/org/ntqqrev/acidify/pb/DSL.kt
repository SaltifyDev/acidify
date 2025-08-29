package org.ntqqrev.acidify.pb

object pb {
    infix fun int32(fieldNumber: Int) = PbInt32(fieldNumber)
    infix fun int64(fieldNumber: Int) = PbInt64(fieldNumber)
    infix fun bool(fieldNumber: Int) = PbBoolean(fieldNumber)
    infix fun string(fieldNumber: Int) = PbString(fieldNumber)
    infix fun bytes(fieldNumber: Int) = PbBytes(fieldNumber)
    infix fun <S : PbSchema> message(schema: S) = `pb message dsl intermediate`(schema)

    class `pb message dsl intermediate`<S : PbSchema>(val schema: S) {
        infix fun field(fieldNumber: Int) = PbMessage(fieldNumber, schema)
    }

    object repeated {
        infix fun int32(fieldNumber: Int) = PbRepeatedInt32(fieldNumber)
        infix fun int64(fieldNumber: Int) = PbRepeatedInt64(fieldNumber)
        infix fun string(fieldNumber: Int) = PbRepeatedString(fieldNumber)
        infix fun bytes(fieldNumber: Int) = PbRepeatedBytes(fieldNumber)
        infix fun <S : PbSchema> message(schema: S) = `repeated pb message dsl intermediate`(schema)

        class `repeated pb message dsl intermediate`<S : PbSchema>(val schema: S) {
            infix fun field(fieldNumber: Int) = PbRepeatedMessage(fieldNumber, schema)
        }
    }

    object optional {
        infix fun int32(fieldNumber: Int) = PbOptional(PbInt32(fieldNumber))
        infix fun int64(fieldNumber: Int) = PbOptional(PbInt64(fieldNumber))
        infix fun string(fieldNumber: Int) = PbOptional(PbString(fieldNumber))
        infix fun bytes(fieldNumber: Int) = PbOptional(PbBytes(fieldNumber))
        infix fun <S : PbSchema> message(schema: S) = `optional pb message dsl intermediate`(schema)

        class `optional pb message dsl intermediate`<S : PbSchema>(val schema: S) {
            infix fun field(fieldNumber: Int) = PbOptional(PbMessage(fieldNumber, schema))
        }
    }
}