package buildsrc.convention

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    mingwX64() // Windows target
    macosX64()
    macosArm64()
    linuxX64()
    linuxArm64()
    jvmToolchain(21)
}
