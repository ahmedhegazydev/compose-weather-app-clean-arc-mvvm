package com.example.simpleweatherapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.simpleweatherapp.R

/**
 * The DetailsScreen composable displays detailed weather information for a specific city.
 *
 * @param description Weather description (e.g., "Sunny").
 * @param temperature Temperature value (e.g., "25°C").
 * @param humidity Humidity percentage (e.g., "60%").
 * @param windSpeed Wind speed value (e.g., "15 km/h").
 * @param iconUrl URL of the weather icon to display.
 * @param cityName Name of the city for which weather data is shown.
 * @param timestamp Timestamp indicating when the weather information was retrieved.
 * @param onDismiss Callback triggered when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    description: String,
    temperature: String,
    humidity: String,
    windSpeed: String,
    iconUrl: String,
    cityName: String,
    timestamp: String,
    onDismiss: () -> Unit,
) {
    Scaffold(
        topBar = {
            CitiesTopAppBar(
                title = cityName,
//                navigationIcon = {
//                    IconButton(onClick = { onDismiss() }) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = stringResource(R.string.back)
//                        )
//                    }
//                },
            )
        }
    ) { paddingValues ->
        WeatherDetailsContent(
            description = description,
            temperature = temperature,
            humidity = humidity,
            windSpeed = windSpeed,
            iconUrl = iconUrl,
            cityName = cityName,
            timestamp = timestamp,
            paddingValues = paddingValues
        )
    }
}

/**
 * Displays the content of the weather details screen, including a header, weather icon, and weather information.
 *
 * @param description Weather description.
 * @param temperature Temperature value.
 * @param humidity Humidity percentage.
 * @param windSpeed Wind speed value.
 * @param iconUrl URL of the weather icon.
 * @param cityName City name.
 * @param timestamp Timestamp of the weather data.
 * @param paddingValues Padding values to respect the scaffold's layout.
 */
@Composable
fun WeatherDetailsContent(
    description: String,
    temperature: String,
    humidity: String,
    windSpeed: String,
    iconUrl: String,
    cityName: String,
    timestamp: String,
    paddingValues: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        WeatherDetailsHeader(cityName = cityName, timestamp = timestamp)
        Spacer(modifier = Modifier.height(16.dp))
        WeatherIcon(iconUrl = iconUrl)
        Spacer(modifier = Modifier.height(16.dp))
        WeatherDetailsInfo(
            description = description,
            temperature = temperature,
            humidity = humidity,
            windSpeed = windSpeed
        )
    }
}

/**
 * Displays a header with the city name and timestamp of the weather data.
 *
 * @param cityName Name of the city.
 * @param timestamp Timestamp of the weather data retrieval.
 */
@Composable
fun WeatherDetailsHeader(cityName: String, timestamp: String) {
    Text(
        text = stringResource(R.string.weather_information_for_received_on, cityName, timestamp),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

/**
 * Displays a weather icon using the provided URL.
 *
 * @param iconUrl URL of the weather icon.
 */
@Composable
fun WeatherIcon(iconUrl: String) {
    AsyncImage(
        model = iconUrl,
        contentDescription = stringResource(R.string.weather_icon),
        modifier = Modifier
            .size(100.dp),
        contentScale = ContentScale.Crop
    )
}

/**
 * Displays detailed weather information, including description, temperature, humidity, and wind speed.
 *
 * @param description Weather description.
 * @param temperature Temperature value.
 * @param humidity Humidity percentage.
 * @param windSpeed Wind speed value.
 */
@Composable
fun WeatherDetailsInfo(
    description: String,
    temperature: String,
    humidity: String,
    windSpeed: String,
) {
    Text(
        stringResource(R.string.description, description),
        style = MaterialTheme.typography.titleLarge
    )
    Text(stringResource(R.string.temp_c, temperature), style = MaterialTheme.typography.titleLarge)
    Text(stringResource(R.string.humidity, humidity), style = MaterialTheme.typography.titleLarge)
    Text(stringResource(R.string.wind_km_h, windSpeed), style = MaterialTheme.typography.titleLarge)
}
