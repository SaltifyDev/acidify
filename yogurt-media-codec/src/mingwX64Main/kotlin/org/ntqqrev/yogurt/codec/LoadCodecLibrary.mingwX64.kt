@file:OptIn(ExperimentalForeignApi::class)

package org.ntqqrev.yogurt.codec

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.reinterpret
import platform.windows.GetProcAddress
import platform.windows.LoadLibraryA

const val dllPath = "LagrangeCodec.x64.dll"

actual fun loadCodecLibrary(): COpaquePointer {
    val handle = LoadLibraryA(dllPath)
    require(handle != null) { "Cannot load DLL $dllPath" }
    return handle
}

actual fun loadCodecFunction(
    handle: COpaquePointer,
    symbol: String
): COpaquePointer {
    val sym = GetProcAddress(handle.reinterpret(), symbol)
    require(sym != null) { "Cannot load symbol $symbol" }
    return sym.reinterpret()
}