plugins {
    id("buildsrc.convention.kotlin-multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinxIO)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}