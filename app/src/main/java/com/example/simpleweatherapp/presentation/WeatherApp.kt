package com.example.simpleweatherapp.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.data.offline.City
import com.example.simpleweatherapp.domain.WeatherViewState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import isLetterOrWhitespace

/**
 * Main WeatherApp Composable that sets up navigation and manages screen transitions.
 * @param viewModel WeatherViewModel instance that handles the data layer.
 */
@Composable
fun WeatherApp(viewModel: WeatherViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "city_list") {
        composable("city_list") {
            CityListScreen(navController, viewModel)
        }

        composable(
            route = "details/{description}/{temperature}/{humidity}/{windSpeed}/{iconUrl}/{cityName}/{timestamp}",
            arguments = createNavArguments("description", "temperature", "humidity", "windSpeed", "iconUrl", "cityName", "timestamp")
        ) { backStackEntry ->
            val weatherDetails = getWeatherDetails(backStackEntry)
            DetailsScreen(
                description = weatherDetails["description"] ?: "N/A",
                temperature = weatherDetails["temperature"] ?: "0.0",
                humidity = weatherDetails["humidity"] ?: "0",
                windSpeed = weatherDetails["windSpeed"] ?: "0.0",
                iconUrl = weatherDetails["iconUrl"] ?: "",
                cityName = weatherDetails["cityName"] ?: "Unknown City",
                timestamp = weatherDetails["timestamp"] ?: ""
            ) {
                navController.navigateUp()
            }
        }

        composable(
            route = "historical/{weather}/{cityName}",
            arguments = createNavArguments("weather", "cityName")
        ) { backStackEntry ->
            val gson = Gson()
            val weatherJson = backStackEntry.arguments?.getString("weather") ?: "{}"
            val weather: WeatherViewState = gson.fromJson(weatherJson, object : TypeToken<WeatherViewState>() {}.type)
            val cityName = backStackEntry.arguments?.getString("cityName") ?: "Unknown City"

            HistoricalWeatherScreen(weather = weather, cityName = cityName) {
                navController.navigateUp()
            }
        }
    }
}

/**
 * Screen displaying a list of cities with options to view details or historical weather.
 * @param navController Navigation controller for handling navigation events.
 * @param viewModel WeatherViewModel instance.
 */
@Composable
fun CityListScreen(navController: NavHostController, viewModel: WeatherViewModel) {
    var selectedCity by remember { mutableStateOf<City?>(null) }
    val cities by viewModel.cities.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var cityName by remember { mutableStateOf("") }

    val navigationEvent by viewModel.navigationEvent.collectAsState()

    Scaffold(
        topBar = {
            CitiesTopAppBar("Cities", rightIcon = { modifier ->
                IconButton(onClick = { showDialog = true }, modifier = modifier) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add City")
                }
            })
        }
    ) { paddingValues ->

        LaunchedEffect(navigationEvent) {
            navigationEvent?.getContentIfNotHandled()?.let { navigationData ->
                navigationData.viewState?.let {
                    handleNavigation(
                        navigationType = navigationData.navigationType,
                        success = it,
                        navController = navController
                    )
                }
            }
        }

        CityListContent(
            cities = cities,
            paddingValues = paddingValues,
            onCitySelected = { selectedCity = it; it?.let { viewModel.navigateToDetails(it) } },
            onCityHistorical = { selectedCity = it; it?.let { viewModel.navigateToHistorical(it) } }
        )

        if (showDialog) {
            AddCityDialog(
                cityName = cityName,
                onCityNameChange = { cityName = it },
                onAddCity = {
                    if (cityName.isNotBlank()) {
                        viewModel.addCity(cityName)
                        cityName = ""
                        showDialog = false
                    }
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}

/**
 * Dialog to add a new city by name.
 * @param cityName Name of the city to be added.
 * @param onCityNameChange Callback to update the city name.
 * @param onAddCity Callback to add the city.
 * @param onDismiss Callback to dismiss the dialog.
 */
@Composable
fun AddCityDialog(
    cityName: String,
    onCityNameChange: (String) -> Unit,
    onAddCity: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_city)) },
        text = {
            Column {
                Text(stringResource(R.string.enter_city_name))
                BasicTextField(
                    value = cityName,
                    onValueChange = { if (it.all { char -> char.isLetterOrWhitespace() }) onCityNameChange(it) },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 16.sp, textAlign = TextAlign.Start)
                )
            }
        },
        confirmButton = { Button(onClick = onAddCity) { Text(stringResource(R.string.add)) } },
        dismissButton = { Button(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}

/**
 * Content composable displaying a scrollable list of cities.
 * @param cities List of City objects to display.
 * @param paddingValues Padding for the LazyColumn.
 * @param onCitySelected Callback when a city is selected for details.
 * @param onCityHistorical Callback when a city is selected for historical weather.
 */
@Composable
fun CityListContent(
    cities: List<City>,
    paddingValues: PaddingValues,
    onCitySelected: (City?) -> Unit,
    onCityHistorical: (City?) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        items(cities) { city ->
            CityRow(
                city = city,
                onCityClick = { onCitySelected(city) },
                onInfoClick = { onCityHistorical(city) }
            )
        }
    }
}

/**
 * Row composable representing a city in the list.
 * @param city City object to display.
 * @param onCityClick Callback for city selection.
 * @param onInfoClick Callback for viewing historical weather.
 */
@Composable
fun CityRow(city: City, onCityClick: () -> Unit, onInfoClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onCityClick)
    ) {
        Text(city.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
        IconButton(onClick = onInfoClick) {
            Icon(painter = painterResource(R.mipmap.info_24px), contentDescription = null, modifier = Modifier.size(24.dp))
        }
    }
}
