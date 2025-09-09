plugins {
    id("buildsrc.convention.kotlin-multiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":acidify-core"))
            implementation(libs.bundles.ktorClient)
            implementation(libs.bundles.ktorServer)
            implementation(libs.ktorSerializationKotlinxJson)
        }
        mingwMain.dependencies {
            implementation(libs.ktorClientWinhttp)
        }
        macosMain.dependencies {
            implementation(libs.ktorClientDarwin)
        }
        linuxMain.dependencies {
            implementation(libs.ktorClientCurl)
        }
    }
}