plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ktor)
}

application {
    mainClass = "MainKt"
}

dependencies {
    implementation(project(":yogurt"))
}