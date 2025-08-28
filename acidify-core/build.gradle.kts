plugins {
    id("buildsrc.convention.kotlin-multiplatform")
    alias(libs.plugins.kotlinPluginSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            project(":acidify-pb")
            implementation(libs.kotlinxIO)
            implementation(libs.kotlinxCoroutines)
            implementation(libs.kotlinxSerialization)
            implementation(libs.ktorClientCore)
            implementation(libs.ktorClientCIO)
            implementation(libs.ktorSerializationKotlinxJson)
            implementation(libs.kmpBigNum)
            implementation(libs.bundles.crypto)
            implementation(libs.kermit)
        }
    }
}