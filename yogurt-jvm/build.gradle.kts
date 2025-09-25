plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ktor)
}

application {
    mainClass = "org.ntqqrev.yogurt.Main"
}

dependencies {
    implementation(project(":yogurt"))
}