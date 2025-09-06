plugins {
    id("buildsrc.convention.kotlin-multiplatform")
    id("org.jetbrains.kotlinx.atomicfu") version "0.29.0"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinxIO)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmTest.dependencies {
            implementation("org.junit.jupiter:junit-jupiter:5.10.0")
            implementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
        }
    }
    tasks.jvmTest {
        useJUnitPlatform()
    }
}