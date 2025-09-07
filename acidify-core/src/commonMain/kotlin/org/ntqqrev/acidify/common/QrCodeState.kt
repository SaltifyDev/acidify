package org.ntqqrev.acidify.common

enum class QrCodeState(val value: Byte) {
    UNKNOWN(-1),
    CONFIRMED(0),
    CODE_EXPIRED(17),
    WAITING_FOR_SCAN(48),
    WAITING_FOR_CONFIRMATION(53),
    CANCELLED(54);

    companion object {
        fun fromByte(value: Byte): QrCodeState = entries.find { it.value == value } ?: UNKNOWN
    }
}