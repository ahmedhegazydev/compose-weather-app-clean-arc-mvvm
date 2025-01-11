package com.example.simpleweatherapp.presentation

import android.net.Uri
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.data.NavigationType
import com.example.simpleweatherapp.data.offline.City
import com.example.simpleweatherapp.domain.WeatherViewState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun WeatherApp(
    viewModel: WeatherViewModel,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "city_list") {
        composable("city_list") { CityListScreen(navController, viewModel) }
        composable(
            route = "details/{description}/{temperature}/{humidity}/{windSpeed}/{iconUrl}/{cityName}/{timestamp}",
            arguments = listOf(
                navArgument("description") { type = NavType.StringType },
                navArgument("temperature") { type = NavType.StringType },
                navArgument("humidity") { type = NavType.StringType },
                navArgument("windSpeed") { type = NavType.StringType },
                navArgument("iconUrl") { type = NavType.StringType },
                navArgument("cityName") { type = NavType.StringType },
                navArgument("timestamp") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val description = backStackEntry.arguments?.getString("description") ?: "N/A"
            val temperature = backStackEntry.arguments?.getString("temperature") ?: "0.0"
            val humidity = backStackEntry.arguments?.getString("humidity") ?: "0"
            val windSpeed = backStackEntry.arguments?.getString("windSpeed") ?: "0.0"
            val iconUrl = backStackEntry.arguments?.getString("iconUrl") ?: ""
            val cityName = backStackEntry.arguments?.getString("cityName") ?: "Unknown City"
            val timestamp = backStackEntry.arguments?.getString("timestamp") ?: ""

            DetailsScreen(
                description,
                temperature,
                humidity,
                windSpeed,
                iconUrl,
                cityName,
                timestamp,
            ) {
                navController.navigateUp()
            }

        }

        composable(
            route = "historical/{weather}/{cityName}",
            arguments = listOf(
                navArgument("weather") { type = NavType.StringType },
                navArgument("cityName") { type = NavType.StringType },


                )
        ) { backStackEntry ->
            val weathersJson = backStackEntry.arguments?.getString("weather") ?: "{}"
            val cityName = backStackEntry.arguments?.getString("cityName") ?: "Unknown City"
            val gson = Gson()
            val weather: WeatherViewState =
                gson.fromJson(weathersJson, object : TypeToken<WeatherViewState>() {}.type)


            HistoricalWeatherScreen(
                weather = weather,
                cityName = cityName,
            ) {
                navController.navigateUp()
            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    navController: NavHostController,
    viewModel: WeatherViewModel,
) {
    var selectedCity by remember { mutableStateOf<City?>(null) }
    val cities by viewModel.cities.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var cityName by remember { mutableStateOf("") }

    val navigationEvent by viewModel.navigationEvent.collectAsState()

    Scaffold(
        topBar = {
            CitiesTopAppBar("Cities",
                rightIcon = { modifier ->
                    IconButton(
                        onClick = { showDialog = true },
                        modifier = modifier
                    ) {

                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add City"
                        )
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
            setSelectedCity = {
                selectedCity = it
                it?.let { city ->
                    viewModel.navigateToDetails(city)
                }

            },

            setHistoricalCity = {
                selectedCity = it
                it?.let { city ->
                    viewModel.navigateToHistorical(city)
                }

            },

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
                    onValueChange = { newValue ->
                        if (newValue.all { it.isLetter() || it.isWhitespace() }) {
                            onCityNameChange(newValue)
                        }
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
        confirmButton = {
            Button(onClick = onAddCity) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}


@Composable
fun CityListContent(
    cities: List<City>,
    paddingValues: PaddingValues,
    setSelectedCity: (City?) -> Unit,
    setHistoricalCity: (City?) -> Unit,
) {
    Column(
        Modifier
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        LazyColumn {
            items(cities) { city ->
                CityRow(
                    city = city,
                    onCityClick = { setSelectedCity(city) },
                    onInfoClick = { setHistoricalCity(city) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}

@Composable
fun CityRow(
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
        Text(
            city.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onInfoClick) {
            Icon(
                painter = painterResource(id = R.mipmap.info_24px),
                contentDescription = "",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

fun handleNavigation(
    navigationType: NavigationType,
    success: WeatherViewState,
    navController: NavHostController,
) {
    val encodedDescription = Uri.encode(success.description)
    val encodedTemperature = Uri.encode(success.temperature)
    val encodedHumidity = Uri.encode(success.humidity)
    val encodedWindSpeed = Uri.encode(success.windSpeed)
    val encodedIconUrl = Uri.encode(success.iconUrl)
    val encodedCityName = Uri.encode(success.cityName)
    val encodedTimestamp = Uri.encode(success.timestamp)

    when (navigationType) {
        NavigationType.DETAILS -> {
            val route =
                "details/$encodedDescription/$encodedTemperature/$encodedHumidity/$encodedWindSpeed/$encodedIconUrl/$encodedCityName/$encodedTimestamp"
            navController.navigate(route)
        }

        NavigationType.HISTORICAL -> {
            val gson = Gson()
            val weathersJson = gson.toJson(success)
            val encodedWeathersJson = Uri.encode(weathersJson)
            navController.navigate("historical/$encodedWeathersJson/${encodedCityName}")
        }

        else -> {}
    }
}

