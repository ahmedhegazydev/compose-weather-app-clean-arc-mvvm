package com.example.simpleweatherapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.simpleweatherapp.ui.theme.SimpleWeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity of the Simple Weather App.
 *
 * This activity serves as the entry point of the application. It is responsible
 * for setting up the UI using Jetpack Compose and initializing the ViewModel.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * ViewModel instance for managing weather-related data and business logic.
     *
     * The ViewModel is provided by Hilt's dependency injection framework and
     * scoped to this activity.
     */
    private val viewModel: WeatherViewModel by viewModels()

    /**
     * Called when the activity is created. Initializes edge-to-edge rendering,
     * sets up the Compose UI, and applies the app's theme.
     *
     * @param savedInstanceState A [Bundle] containing the saved state of the activity
     * if it was previously paused or stopped.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge rendering for a more immersive UI experience.
//        enableEdgeToEdge()

        // Set the content view using Jetpack Compose.
        setContent {
            // Apply the app's theme and render the WeatherApp composable.
            SimpleWeatherAppTheme {
                WeatherApp(viewModel = viewModel)
            }
        }
    }
}
