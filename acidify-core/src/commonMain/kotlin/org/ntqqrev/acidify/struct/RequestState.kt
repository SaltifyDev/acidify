package org.ntqqrev.acidify.struct

enum class RequestState(val value: Int) {
    DEFAULT(0),
    PENDING(1),
    ACCEPTED(2),
    REJECTED(3);

    companion object {
        fun from(value: Int): RequestState {
            return entries.firstOrNull { it.value == value } ?: DEFAULT
        }
    }
}