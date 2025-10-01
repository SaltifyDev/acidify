@file:OptIn(ExperimentalForeignApi::class)

package org.ntqqrev.yogurt.codec

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.RTLD_LAZY
import platform.posix.dlopen
import platform.posix.dlsym

const val dylibPath = "./liblagrangecodec.dylib"

actual fun loadCodecLibrary(): COpaquePointer {
    val handle = dlopen(dylibPath, RTLD_LAZY)
    require(handle != null) { "Cannot load shared library $dylibPath" }
    return handle
}

actual fun loadCodecFunction(
    handle: COpaquePointer,
    symbol: String
): COpaquePointer {
    val sym = dlsym(handle, symbol)
    require(sym != null) { "Cannot load symbol $symbol" }
    return sym
}