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
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}