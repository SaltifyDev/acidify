plugins {
    id("buildsrc.convention.kotlin-multiplatform")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":acidify-crypto"))
            implementation(project(":acidify-pb"))
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.io)
            implementation(libs.kotlinx.datetime)
            implementation(libs.bundles.ktor.client)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.bundles.korlibs)
            implementation(libs.bundles.xmlutil)
            implementation(libs.stately.concurrent.collections)
            implementation(libs.mordant)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        mingwMain.dependencies {
            implementation(libs.ktor.client.winhttp)
        }
        macosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        linuxMain.dependencies {
            implementation(libs.ktor.client.curl)
        }
    }
}