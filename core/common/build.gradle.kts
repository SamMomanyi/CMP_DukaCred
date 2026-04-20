import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.buildKonfig)
}

val secretsProperties = Properties().apply {
    val secretsFile = rootProject.file("secrets/secrets.properties")
    if (secretsFile.exists()) {
        secretsFile.inputStream().use(::load)
    }
}
// 1. Load the properties file safely
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

// 2. Configure BuildKonfig to generate the BuildKonfig object
buildkonfig {
    // Must match your app's namespace
    packageName = "com.samduka.dukacred"

    defaultConfigs {
        // Supabase URL
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "SUPABASE_URL",
            localProperties.getProperty("SUPABASE_URL") ?: "MISSING_URL"
        )

        // Supabase Anon Key
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "SUPABASE_ANON_KEY",
            localProperties.getProperty("SUPABASE_ANON_KEY") ?: "MISSING_KEY"
        )
    }
}
kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
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

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
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

buildkonfig {
    packageName = "com.samduka.dukacred.core.common.config"
    exposeObjectWithName = "AppConfig"

    defaultConfigs {
        buildConfigField(STRING, "SUPABASE_URL", secretsProperties.getProperty("SUPABASE_URL", ""))
        buildConfigField(STRING, "SUPABASE_ANON_KEY", secretsProperties.getProperty("SUPABASE_ANON_KEY", ""))
    }
}
