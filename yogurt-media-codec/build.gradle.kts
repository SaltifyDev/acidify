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
            implementation("io.github.kasukusakura:silk-codec:0.0.5")
            implementation("com.googlecode.soundlibs:mp3spi:1.9.5.4")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}