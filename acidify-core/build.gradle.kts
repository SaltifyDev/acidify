plugins {
    id("buildsrc.convention.kotlin-multiplatform")
    alias(libs.plugins.kotlinPluginSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":acidify-crypto"))
            implementation(project(":acidify-pb"))
            implementation(libs.kotlinxIO)
            implementation(libs.kotlinxCoroutines)
            implementation(libs.kotlinxSerialization)
            implementation(libs.ktorClientCore)
            implementation(libs.ktorClientCIO)
            implementation(libs.ktorClientContentNegotiation)
            implementation(libs.ktorSerializationKotlinxJson)
            implementation(libs.bundles.korlibs)
            implementation(libs.kermit)
            implementation(libs.mordant)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmMain.dependencies {
            implementation(libs.slf4jNop)
        }
        mingwMain.dependencies {
            implementation(libs.ktorClientWinhttp)
        }
        macosMain.dependencies {
            implementation(libs.ktorClientDarwin)
        }
        linuxMain.dependencies {
            implementation(libs.ktorClientCurl)
        }
    }
}