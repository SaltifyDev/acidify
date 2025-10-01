plugins {
    id("buildsrc.convention.kotlin-multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":acidify-core"))
            implementation(libs.kotlinx.io)
        }
        jvmMain.dependencies {
            implementation("net.java.dev.jna:jna:5.18.0")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}