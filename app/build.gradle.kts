import com.android.build.api.dsl.ApkSigningConfig
import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
}

configure<ApplicationExtension> {
    namespace = "com.trevorwiebe.apogee"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.trevorwiebe.apogee"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            configureSigning()
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }

    testOptions {
        suites {
            create("journeysTest") {
                assets {
                }
                targets {
                    create("default") {
                    }
                }
                useJunitEngine {
                    inputs += listOf(com.android.build.api.dsl.AgpTestSuiteInputParameters.TESTED_APKS)
                    includeEngines += listOf("journeys-test-engine")
                    enginesDependencies(libs.junit.platform.launcher)
                    enginesDependencies(libs.junit.platform.engine)
                    enginesDependencies(libs.journeys.junit.engine)
                }
                targetVariants += listOf("debug")
            }
        }
    }
}

kotlin{
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        freeCompilerArgs.addAll(listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-XexplicitApi=strict"
        ))
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.firebase.firestore)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    implementation(libs.androidx.hilt.navigation.compose)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

fun ApkSigningConfig.configureSigning() {
    val localKeyStorePath = System.getenv("KEY_STORE_PATH_LOCAL")
    val ciKeyStorePath = System.getenv("KEY_STORE_PATH_CI")

    val resolvedKeystorePath = when {
        !localKeyStorePath.isNullOrEmpty() -> localKeyStorePath
        !ciKeyStorePath.isNullOrEmpty() -> ciKeyStorePath
        else -> throw GradleException("No valid keystore path found in environment variables.")
    }

    val resolvedKeystoreFile = File(resolvedKeystorePath)

    if (!resolvedKeystoreFile.exists()) {
        throw GradleException("Keystore file not found at: $resolvedKeystorePath")
    }

    storeFile = resolvedKeystoreFile
    storePassword = System.getenv("KEY_STORE_PASSWORD")
    keyAlias = System.getenv("SIGNING_KEY_ALIAS") ?: "key0"
    keyPassword = System.getenv("KEY_PASSWORD")

    if (storePassword.isNullOrBlank()) {
        throw GradleException("Environment variable KEY_STORE_PASSWORD is missing or blank.")
    }
    if (keyAlias.isNullOrBlank()) {
        throw GradleException("Environment variable SIGNING_KEY_ALIAS is missing or blank.")
    }
    if (keyPassword.isNullOrBlank()) {
        throw GradleException("Environment variable KEY_PASSWORD is missing or blank.")
    }
}