package com.example.simpleweatherapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.domain.WeatherDescription
import com.example.simpleweatherapp.domain.WeatherViewState

/**
 * A composable screen for displaying historical weather data for a specific city.
 *
 * @param weather The state object containing the list of historical weather data.
 * @param cityName Name of the city for which historical data is displayed.
 * @param onDismiss Callback triggered when the "Done" button is pressed to dismiss the screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalWeatherScreen(
    weather: WeatherViewState, // Weather data containing a list of weather items.
    cityName: String, // Name of the city.
    onDismiss: () -> Unit, // Dismiss callback.
) {
    Scaffold(
        topBar = {
            // TopAppBar with a "Done" button for navigation.
            CitiesTopAppBar(
                title = stringResource(R.string.historical, cityName),
//                navigationIcon = {
//                    TextButton(
//                        onClick = { onDismiss() },
//                        modifier = Modifier.padding(start = 8.dp)
//                    ) {
//                        Text(
//                            text = stringResource(R.string.done),
//                            color = Color.Black,
//                            style = MaterialTheme.typography.titleMedium
//                        )
//                    }
//                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize() // Fills the entire screen.
                .padding(paddingValues), // Applies padding from Scaffold.
            horizontalAlignment = Alignment.CenterHorizontally // Centers content horizontally.
        ) {
            // LazyColumn for displaying a scrollable list of weather items.
            LazyColumn {
                items(weather.weathers) { weatherDescription ->
                    HistoricalWeatherItem(
                        dateTime = weather.timestamp, // Date and time of the weather event.
                        temperature = weather.temperature, // Temperature during the event.
                        weatherDescription = weatherDescription.description // Description of the weather.
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHistoricalWeatherScreen() {
    HistoricalWeatherScreen(
        weather = WeatherViewState(
            description = "Clear skies",
            temperature = "25°C",
            humidity = "50%",
            windSpeed = "10 km/h",
            iconUrl = "",
            cityName = "Cairo",
            timestamp = "2023-01-01 10:00",
            location = "Egypt",
            weathers = listOf(
                WeatherDescription(
                    id = 800,
                    main = "Clear",
                    description = "Clear sky",
                    icon = "01d"
                ),
                WeatherDescription(
                    id = 800,
                    main = "Clear",
                    description = "Clear sky",
                    icon = "01d"
                )
            )
        ),
        cityName = "Cairo",
        onDismiss = {}
    )
}


/**
 * A composable function to display an individual historical weather item.
 *
 * @param dateTime The date and time of the weather event.
 * @param temperature The temperature recorded during the weather event.
 * @param weatherDescription A description of the weather (e.g., "Rainy").
 */
@Composable
fun HistoricalWeatherItem(
    dateTime: String, // Date and time of the weather event.
    temperature: String, // Recorded temperature.
    weatherDescription: String, // Description of the weather.
) {
    Row(
        modifier = Modifier
            .fillMaxWidth() // Fills the available width.
            .padding(16.dp) // Adds padding around the item.
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // Displays the date and time of the weather event.
            Text(dateTime, style = MaterialTheme.typography.titleLarge)

            // Displays the weather description and temperature.
            Text("$weatherDescription, $temperature", style = MaterialTheme.typography.titleLarge)
        }

        // Icon indicating details, aligned vertically to the center.
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = stringResource(R.string.details),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
