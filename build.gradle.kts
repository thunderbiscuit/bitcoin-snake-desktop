import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.20"
    id("org.jetbrains.compose") version "1.10.0-alpha03"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.20"
    // id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
}

repositories {
    mavenCentral()
    google()
    mavenLocal()
}

group = "me.tb"
version = "1.0.0-SNAPSHOT"

dependencies {
    // Compose
    implementation(compose.runtime)
    implementation(compose.components.resources)
    implementation(compose.desktop.currentOs)
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.components.uiToolingPreview)

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    // Navigation 3
    implementation("org.jetbrains.androidx.navigation3:navigation3-ui-desktop:1.0.0-alpha03")

    // composables.com
    implementation("com.composables:core:1.46.0")
    implementation("com.composables:icons-lucide:1.0.0")

    // QR Codes
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")

    // Bitcoin
    implementation("org.bitcoindevkit:bdk-jvm:2.1.0-SNAPSHOT")
    implementation("org.kotlinbitcointools:bip21:0.1.0")

    // Tests
    implementation("org.jetbrains.kotlin:kotlin-test:2.2.20")
}

compose.resources {
    customDirectory(
        sourceSetName = "main",
        directoryProvider = provider { layout.projectDirectory.dir("src/main/resources/compose/") }
    )
}

compose.desktop {
    application {
        mainClass = "me.tb.bitcoinsnake.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Bitcoin Snake"
            packageVersion = "1.0.0"

            // macOS {
            //     iconFile.set(project.file("Godzilla.icns"))
            // }
        }
    }
}
