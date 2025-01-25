plugins {
    alias(notation = libs.plugins.android.library)
    alias(notation = libs.plugins.kotlin.android)
    alias(notation = libs.plugins.compose.compiler)
    alias(notation = libs.plugins.about.libraries)
    `maven-publish`
}

android {

    namespace = "com.d4rk.android.libs.apptoolkit"
    compileSdk = 35

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile(name = "proguard-android-optimize.txt") , "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    //AndroidX
    api(dependencyNotation = libs.androidx.core.ktx)
    api(dependencyNotation = libs.androidx.appcompat)
    api(dependencyNotation = libs.androidx.core.splashscreen)
    api(dependencyNotation = libs.androidx.multidex)
    api(dependencyNotation = libs.androidx.work.runtime.ktx)

    // Compose
    api(dependencyNotation = platform(libs.androidx.compose.bom))
    api(dependencyNotation = libs.androidx.ui)
    api(dependencyNotation = libs.androidx.activity.compose)
    api(dependencyNotation = libs.androidx.ui.graphics)
    api(dependencyNotation = libs.androidx.compose.runtime)
    api(dependencyNotation = libs.androidx.runtime.livedata)
    api(dependencyNotation = libs.androidx.ui.tooling.preview)
    api(dependencyNotation = libs.androidx.material3)
    api(dependencyNotation = libs.androidx.material.icons.extended)
    api(dependencyNotation = libs.datastore.preferences)
    api(dependencyNotation = libs.androidx.datastore.preferences)
    api(dependencyNotation = libs.androidx.foundation)
    api(dependencyNotation = libs.androidx.navigation.compose)

    // Google
    api(dependencyNotation = libs.play.services.ads)
    api(dependencyNotation = libs.material)

    // Images
    api(dependencyNotation = libs.coil.compose)
    api(dependencyNotation = libs.coil.gif)
    api(dependencyNotation = libs.coil.network.okhttp)

    // Kotlin
    api(dependencyNotation = libs.kotlinx.coroutines.android)
    api(dependencyNotation = libs.kotlinx.serialization.json)

    // Ktor
    api(dependencyNotation = platform(libs.ktor.bom))
    api(dependencyNotation = libs.ktor.client.android)
    api(dependencyNotation = libs.ktor.client.serialization)
    api(dependencyNotation = libs.ktor.client.logging)
    api(dependencyNotation = libs.ktor.client.content.negotiation)
    api(dependencyNotation = libs.ktor.serialization.kotlinx.json)

    // Lifecycle
    api(dependencyNotation = libs.androidx.lifecycle.runtime.ktx)
    api(dependencyNotation = libs.androidx.lifecycle.livedata.ktx)
    api(dependencyNotation = libs.androidx.lifecycle.process)
    api(dependencyNotation = libs.androidx.lifecycle.viewmodel.ktx)
    api(dependencyNotation = libs.androidx.lifecycle.viewmodel.compose)
    api(dependencyNotation = libs.androidx.lifecycle.runtime.compose)

    // About
    api(dependencyNotation = libs.aboutlibraries)
    api(dependencyNotation = libs.core)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.D4rK7355608"
            artifactId = "AppToolkit"
            version = "0.0.37"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}