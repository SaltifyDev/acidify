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
            implementation(libs.ktor.network)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.korlibs.compression)
            implementation(libs.kotlincrypto.hash.sha1)
            implementation(libs.bundles.xmlutil)
            implementation(libs.stately.concurrent.collections)
            implementation(libs.mordant)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmMain.dependencies {
            implementation("net.covers1624:curl4j:3.0-SNAPSHOT:libcurl")
            implementation("net.covers1624:Quack:0.4.10.117")
        }
        nativeMain.dependencies {
            implementation(libs.ktor.client.curl)
        }
    }
}