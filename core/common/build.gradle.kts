plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "CoreCommon"
            isStatic = true
        }
    }

    jvm() // For the Desktop app

    sourceSets {
        commonMain.dependencies {
            // Core concurrency
            implementation(libs.kotlinx.coroutines.core)

            // For UserSession and domain models dealing with time
            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            // Android-specific common dependencies can go here if needed later
        }

        val iosMain by creating {
            dependencies {
                // iOS-specific common dependencies
            }
        }

        val jvmMain by getting {
            dependencies {
                // Desktop-specific common dependencies
            }
        }
    }
}

android {
    namespace = "com.samduka.dukacred.core.common"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}