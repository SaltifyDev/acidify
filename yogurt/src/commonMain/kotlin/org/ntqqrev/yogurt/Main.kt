@file:JvmName("Main")

package org.ntqqrev.yogurt

import org.ntqqrev.yogurt.util.addSigIntHandler
import kotlin.jvm.JvmName

fun main() {
    println("""
        | .--------------------------------------.
        | |   __  __                       __    |
        | |   \ \/ /___  ____ ___  _______/ /_   |
        | |    \  / __ \/ __ `/ / / / ___/ __/   |
        | |    / / /_/ / /_/ / /_/ / /  / /_     |
        | |   /_/\____/\__, /\__,_/_/   \__/     |
        | |           /____/   Acidify + Milky   |
        | '--------------------------------------'
    """.trimMargin())
    YogurtApp.createServer()
        .addSigIntHandler()
        .start(wait = true)
}