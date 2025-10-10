package buildsrc.convention

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    val actionsTarget = System.getenv("GITHUB_ACTIONS_BUILD_TARGET")
    val hostOs = System.getProperty("os.name")
    val arch = System.getProperty("os.arch")
    when {
        // is run from GitHub Actions
        actionsTarget == "mingwX64" -> mingwX64()
        actionsTarget == "macosArm64" -> macosArm64()
        actionsTarget == "linuxX64" -> linuxX64()
        actionsTarget == "linuxArm64" -> linuxArm64()

        // is run locally
        hostOs == "Mac OS X" && arch == "aarch64" -> macosArm64()
        hostOs == "Linux" && arch == "x86_64" -> linuxX64()
        hostOs.startsWith("Windows") -> mingwX64()
    }

    jvmToolchain(21)
}
