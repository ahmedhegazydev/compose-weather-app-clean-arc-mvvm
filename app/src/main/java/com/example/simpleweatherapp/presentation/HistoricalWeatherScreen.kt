package com.example.simpleweatherapp.presentation;

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.domain.WeatherViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalWeatherScreen(
    weather: WeatherViewState,
    cityName: String,
    onDismiss: () -> Unit,
) {


    Scaffold(
        topBar = {
            CitiesTopAppBar(
                title = stringResource(R.string.historical, cityName),
                navigationIcon = {
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Done",
                            color = Color.Black,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            LazyColumn {
                items(weather.weathers) { weatherDescription ->
                    HistoricalWeatherItem(
                        weather.timestamp,
                        weather.temperature,
                        weatherDescription.description
                    )
                }
            }
        }
    }
}

@Composable
fun HistoricalWeatherItem(
    dateTime: String,
    temperature: String,
    weatherDescription: String,

    ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(dateTime, style = MaterialTheme.typography.titleLarge)
            Text("$weatherDescription, $temperature", style = MaterialTheme.typography.titleLarge)
        }

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = stringResource(R.string.details),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
