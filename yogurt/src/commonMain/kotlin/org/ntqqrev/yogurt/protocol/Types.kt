// Manually written Milky protocol definition
// Later will be replaced with generated code

package org.ntqqrev.yogurt.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

private typealias Sn = SerialName

const val milkyVersion = "1.0"
const val milkyPackageVersion = "1.0.0-draft.14"

val milkyJsonModule = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

// ################################
// API Types
// ################################

@Serializable
class ApiEmptyStruct

@Serializable
class GetLoginInfoResponse(
    @Sn("uin") val uin: Long,
    @Sn("nickname") val nickname: String,
)

@Serializable
class ApiGeneralResponse(
    @Sn("status") val status: String,
    @Sn("retcode") val retcode: Int,
    @Sn("data") val data: JsonElement? = null,
    @Sn("message") val message: String? = null,
)