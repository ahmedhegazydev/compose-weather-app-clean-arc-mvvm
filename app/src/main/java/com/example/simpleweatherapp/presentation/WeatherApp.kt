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
import com.example.simpleweatherapp.data.NavigationType
import com.example.simpleweatherapp.data.offline.City
import com.example.simpleweatherapp.domain.WeatherViewState
import com.example.simpleweatherapp.presentation.WeatherViewModel.NavigationState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import isLetterOrWhitespace

/**
 * Entry point of the WeatherApp.
 *
 * Sets up navigation using [NavHost] and displays screens like [CityListScreen], [DetailsScreen], and [HistoricalWeatherScreen].
 *
 * @param viewModel The instance of [WeatherViewModel] responsible for managing UI state and navigation.
 */
@Composable
fun WeatherApp(viewModel: WeatherViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "city_list") {
        // Composable for displaying the list of cities
        composable("city_list") {
            CityListScreen(navController, viewModel)
        }

        // Composable for showing details about the weather
        composable(
            route = "details/{description}/{temperature}/{humidity}/{windSpeed}/{iconUrl}/{cityName}/{timestamp}",
            arguments = createNavArguments(
                "description",
                "temperature",
                "humidity",
                "windSpeed",
                "iconUrl",
                "cityName",
                "timestamp"
            )
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

        // Composable for displaying historical weather data
        composable(
            route = "historical/{weather}/{cityName}",
            arguments = createNavArguments("weather", "cityName")
        ) { backStackEntry ->
            val gson = Gson()
            val weatherJson = backStackEntry.arguments?.getString("weather") ?: "{}"
            val weather: WeatherViewState =
                gson.fromJson(weatherJson, object : TypeToken<WeatherViewState>() {}.type)
            val cityName = backStackEntry.arguments?.getString("cityName") ?: "Unknown City"

            HistoricalWeatherScreen(weather = weather, cityName = cityName) {
                navController.navigateUp()
            }
        }
    }
}

/**
 * Displays a list of cities and handles navigation to weather details or historical weather.
 *
 * @param navController Controller for handling navigation events.
 * @param viewModel The instance of [WeatherViewModel] responsible for managing the list of cities and navigation state.
 */
@Composable
fun CityListScreen(navController: NavHostController, viewModel: WeatherViewModel) {
    var loadingCity by remember { mutableStateOf<City?>(null) }
    val cities by viewModel.cities.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var cityName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val navigationEvent by viewModel.navigationSingedEvent.collectAsState()

    Scaffold(
        topBar = {
            CitiesTopAppBar("Cities", rightIcon = { modifier ->
                IconButton(onClick = { showDialog = true }, modifier = modifier) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add City")
                }
            })
        },
    ) { paddingValues ->
        Column {

            // Observe navigation events
            LaunchedEffect(navigationEvent) {
                navigationEvent?.getContentIfNotHandled()?.let { state ->
                    when (state) {
                        is NavigationState.Loading -> {
                            println("Loading...")
                        }
                        is NavigationState.Success -> {
                            state.data.viewState?.let {
                                handleNavigation(
                                    navigationType = state.data.navigationType,
                                    success = it,
                                    navController = navController
                                )
                            }
                        }
                        is NavigationState.Error -> {
                            errorMessage = state.message
                        }
                        NavigationState.Idle -> {
                            // No operation
                        }
                    }
                }
            }

            // Display city list content
            CityListContent(
                loadingCity,
                cities = cities,
                paddingValues = paddingValues,
                onCitySelected = {
                    loadingCity = it
                    it?.let { viewModel.navigate(it, NavigationType.DETAILS) }
                },
                onCityHistorical = {
                    loadingCity = it
                    it?.let { viewModel.navigate(it, NavigationType.HISTORICAL) }
                }
            )

            // Show error message
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Dialog for adding a new city
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
}

/**
 * Displays the dialog for adding a new city to the list.
 *
 * @param cityName The name of the city to add.
 * @param onCityNameChange Callback to update the city name entered by the user.
 * @param onAddCity Callback to confirm adding the new city.
 * @param onDismiss Callback to dismiss the dialog.
 */
@Composable
fun AddCityDialog(
    cityName: String,
    onCityNameChange: (String) -> Unit,
    onAddCity: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_city)) },
        text = {
            Column {
                Text(stringResource(R.string.enter_city_name))
                BasicTextField(
                    value = cityName,
                    onValueChange = {
                        if (it.all { char -> char.isLetterOrWhitespace() }) onCityNameChange(
                            it
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start
                    )
                )
            }
        },
        confirmButton = { Button(onClick = onAddCity) { Text(stringResource(R.string.add)) } },
        dismissButton = { Button(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}

/**
 * Displays a scrollable list of cities.
 *
 * @param cities List of city objects to display.
 * @param paddingValues Padding for the list layout.
 * @param onCitySelected Callback triggered when a city is selected.
 * @param onCityHistorical Callback triggered when historical weather for a city is requested.
 */
@Composable
fun CityListContent(
    loadingCity: City?,
    cities: List<City>,
    paddingValues: PaddingValues,
    onCitySelected: (City?) -> Unit,
    onCityHistorical: (City?) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        items(cities) { city ->
            CityRow(
                showLoader = city == loadingCity,
                city = city,
                onCityClick = { onCitySelected(city) },
                onInfoClick = { onCityHistorical(city) }
            )
        }
    }
}

/**
 * Displays a single row representing a city in the list.
 *
 * @param city The city object to display.
 * @param onCityClick Callback triggered when the city is clicked.
 * @param onInfoClick Callback triggered when historical weather is requested.
 */
@Composable
fun CityRow(
    showLoader: Boolean = false,
    city: City,
    onCityClick: () -> Unit,
    onInfoClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onCityClick)
    ) {
        Text(city.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))

        if (showLoader) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else
            IconButton(onClick = onInfoClick) {
                Icon(
                    painter = painterResource(R.mipmap.info_24px),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
    }
}
