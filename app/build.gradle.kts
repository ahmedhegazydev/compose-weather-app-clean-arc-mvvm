plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")

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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

//    testOptions {
//        unitTests.all {
//            packagingOptions {
//                exclude("META-INF/LICENSE.md")
//                exclude("META-INF/LICENSE.txt")
//                exclude("META-INF/NOTICE.md")
//                exclude("META-INF/NOTICE.txt")
//            }
//        }
//    }

}

val kotlinVersion = "1.9.10"
val roomVersion = "2.6.1"
val hiltVersion = "2.55"


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.core.testing)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Retrofit for API
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.logging.interceptor)


    implementation( libs.androidx.navigation.compose)
    implementation( libs.coil.compose)

    implementation( "org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.6.0")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    // Hilt
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    implementation( libs.glide )
    implementation( libs.ui)
    kapt("com.github.bumptech.glide:compiler:4.15.1")


    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockk)
    testImplementation(libs.slf4j.simple)

    testImplementation( libs.mockwebserver)
    testImplementation(  libs.androidx.room.testing)
    testImplementation(  libs.androidx.core.testing)

    androidTestImplementation (libs.hilt.android.testing)
    androidTestImplementation (libs.mockwebserver)

    testImplementation(  libs.mockito.inline)


    // For Compose Testing
    androidTestImplementation (libs.ui.test.junit4)
    debugImplementation (libs.ui.tooling)
    debugImplementation (libs.ui.test.manifest)

    implementation(libs.kotlin.reflect)

    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation (libs.mockk.android)


    androidTestImplementation("org.junit.jupiter:junit-jupiter:5.8.2") {
        exclude(group = "META-INF", module = "LICENSE.md")
    }

    kaptAndroidTest(libs.hilt.android.compiler)
    kaptAndroidTest(libs.androidx.room.compiler)


}

kapt {
    correctErrorTypes = true
    useBuildCache = false
    showProcessorStats = true
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.6.0")
    }
}
