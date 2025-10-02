package org.ntqqrev.acidify.internal.packet.message.misc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.UnknownChildHandler
import nl.adaptivity.xmlutil.serialization.XML

@Serializable
data class IncomingForwardBody(
    @SerialName("m_resid") val resId: String,
) {
    companion object {
        @OptIn(ExperimentalXmlUtilApi::class)
        val xmlModule = XML {
            defaultPolicy {
                unknownChildHandler = UnknownChildHandler {
                        _, _, _, _, _ -> emptyList()
                }
            }
        }
    }
}