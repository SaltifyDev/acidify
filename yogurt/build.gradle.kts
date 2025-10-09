import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("buildsrc.convention.kotlin-multiplatform")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":acidify-core"))
            implementation(project(":yogurt-media-codec"))
            implementation(project(":yogurt-qrcode"))
            implementation(libs.bundles.ktor.client)
            implementation(libs.bundles.ktor.server)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.stately.concurrent.collections)
            implementation(libs.mordant)
        }
        jvmMain.dependencies {
            implementation(libs.logback.classic)
        }
        nativeMain.dependencies {
            implementation(libs.ktor.client.curl)
        }
    }

    targets.withType<KotlinNativeTarget> {
        binaries {
            executable {
                entryPoint = "org.ntqqrev.yogurt.main"
            }
        }
    }
}