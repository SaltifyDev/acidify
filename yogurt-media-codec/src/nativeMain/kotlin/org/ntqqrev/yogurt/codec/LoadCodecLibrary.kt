@file:OptIn(ExperimentalForeignApi::class)

package org.ntqqrev.yogurt.codec

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi

expect fun loadCodecLibrary(): COpaquePointer

expect fun loadCodecFunction(handle: COpaquePointer, symbol: String): COpaquePointer