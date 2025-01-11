package com.example.simpleweatherapp.presentation;

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.simpleweatherapp.R


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
                navigationIcon = {
                    IconButton(onClick = { onDismiss() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
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

@Composable
fun WeatherDetailsHeader(cityName: String, timestamp: String) {
    Text(
        text = stringResource(R.string.weather_information_for_received_on, cityName, timestamp),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

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
