plugins {
    id("buildsrc.convention.kotlin-multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":acidify-core"))
            implementation(libs.kotlinx.io)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}