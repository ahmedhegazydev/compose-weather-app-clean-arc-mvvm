package com.example.simpleweatherapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * MyApp is the application class for the SimpleWeatherApp.
 *
 * This class is annotated with `@HiltAndroidApp`, which triggers Hilt's code generation.
 * It serves as the root of the dependency injection graph for the application.
 *
 * By extending the `Application` class, it ensures that Hilt's components are properly
 * initialized at the start of the application lifecycle.
 */
@HiltAndroidApp
class MyApp : Application()
