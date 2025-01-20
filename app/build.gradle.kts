plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")

}

android {
    namespace = "com.example.simpleweatherapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.simpleweatherapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.example.simpleweatherapp.di.CustomTestRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true

    }

    defaultConfig {
        buildConfigField("String", "API_KEY", "\"f5cb0b965ea1564c50c6f1b74534d823\"")
        buildConfigField("String", "BASE_URL", "\"https://api.openweathermap.org\"")
    }

    composeOptions {
        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.15"
        }
    }

    packaging {
        resources {
            excludes.addAll(
                listOf(
                    "META-INF/LICENSE.md",
                    "META-INF/LICENSE.txt",
                    "META-INF/NOTICE.md",
                    "META-INF/NOTICE.txt",
                    "META-INF/LICENSE-notice.md",

                    )
            )
        }
    }


}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.core.testing)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // Retrofit for API
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.logging.interceptor)


    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)

    implementation(libs.kotlinx.metadata.jvm)

    // Kotlin
    implementation(libs.kotlin.stdlib.v1910)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Hilt
    implementation(libs.hilt.android.v255)
    ksp(libs.hilt.compiler)

    implementation(libs.glide)
    implementation(libs.ui)
    ksp(libs.compiler)


    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockk)
    testImplementation(libs.slf4j.simple)

    testImplementation(libs.mockwebserver)
    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.androidx.core.testing)

    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.mockwebserver)

    testImplementation(libs.mockito.inline)


    // For Compose Testing
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.kotlin.reflect)

    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.mockk.android)


    androidTestImplementation("org.junit.jupiter:junit-jupiter:5.8.2") {
        exclude(group = "META-INF", module = "LICENSE.md")
    }


    testImplementation(libs.byte.buddy)


}


configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0")
    }
}



tasks.withType<Test> {
    jvmArgs = (jvmArgs ?: mutableListOf()) + "-Dnet.bytebuddy.experimental=true"
}
