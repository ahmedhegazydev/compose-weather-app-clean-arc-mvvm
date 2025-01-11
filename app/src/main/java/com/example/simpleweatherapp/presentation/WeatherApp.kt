package com.example.simpleweatherapp.presentation

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
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
                navController = navController

            )

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
fun HistoricalWeatherScreen(
    weather: WeatherViewState,
    cityName: String,
    onDismiss: () -> Unit,

    ) {
    Scaffold(
        topBar = {
            TopAppBar(

                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$cityName Historical")
                    }
                },
                navigationIcon = {
                    TextButton(
                        modifier =
                        Modifier.border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = MaterialTheme.shapes.small
                        ),
                        onClick = { onDismiss() }) {
                        Text(
                            text = "Done",
                            color = Color.Black,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

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
            contentDescription = "Details",
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    navController: NavHostController,
    viewModel: WeatherViewModel,
) {
    var selectedCity by remember { mutableStateOf<City?>(null) }
    val weatherState by viewModel.weatherState.collectAsState()
    val cities by viewModel.cities.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var cityName by remember { mutableStateOf("") }
    val navigationType by viewModel.navigationType.collectAsState()

    Scaffold(
        topBar = { CitiesTopAppBar { showDialog = true } }
    ) { paddingValues ->
        CityListContent(
            cities = cities,
            weatherState = weatherState,
            selectedCity = selectedCity,
            setSelectedCity = { selectedCity = it },
            navigationType = navigationType,
            viewModel = viewModel,
            navController = navController,
            paddingValues = paddingValues
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
        title = { Text("Add City") },
        text = {
            Column {
                Text("Enter city name:")
                BasicTextField(
                    value = cityName,
                    onValueChange = onCityNameChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = onAddCity) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitiesTopAppBar(onAddCityClick: () -> Unit) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Cities",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center)
                )

                IconButton(
                    onClick = onAddCityClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add City"
                    )
                }
            }
        },
        navigationIcon = {}
    )
}

@Composable
fun CityListContent(
    cities: List<City>,
    weatherState: WeatherViewModel.WeatherState,
    selectedCity: City?,
    setSelectedCity: (City) -> Unit,
    navigationType: NavigationType,
    viewModel: WeatherViewModel,
    navController: NavHostController,
    paddingValues: PaddingValues,
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
                    onCityClick = {
                        viewModel.setNavigationType(NavigationType.DETAILS)
                        setSelectedCity(city)
                        viewModel.fetchWeather(city)
                    },
                    onInfoClick = {
                        viewModel.setNavigationType(NavigationType.HISTORICAL)
                        setSelectedCity(city)
                        viewModel.fetchWeather(city)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (weatherState) {
            is WeatherViewModel.WeatherState.Success -> {
                val success = weatherState.viewState
                LaunchedEffect(navigationType, selectedCity) {
                    handleNavigation(
                        navigationType = navigationType,
                        success = success,
                        navController = navController
                    )
                }
            }

            is WeatherViewModel.WeatherState.Error -> {
                Text("Error: ${weatherState.message}")
            }

            else -> {}
        }
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
    navController: NavHostController,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(cityName)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Weather information for $cityName received on $timestamp",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            AsyncImage(
                model = iconUrl,
                contentDescription = "Weather Icon",
                modifier = Modifier
                    .size(100.dp),
                contentScale = ContentScale.Crop

            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Description: $description", style = MaterialTheme.typography.titleLarge)
            Text("Temp: $temperature°C", style = MaterialTheme.typography.titleLarge)
            Text("Humidity: $humidity%", style = MaterialTheme.typography.titleLarge)
            Text("Wind: $windSpeed km/h", style = MaterialTheme.typography.titleLarge)
        }
    }
}

