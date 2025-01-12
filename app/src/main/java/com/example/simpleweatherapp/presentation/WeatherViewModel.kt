package com.example.simpleweatherapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpleweatherapp.data.NavigationType
import com.example.simpleweatherapp.data.offline.City
import com.example.simpleweatherapp.domain.AddCityUseCase
import com.example.simpleweatherapp.domain.GetCitiesUseCase
import com.example.simpleweatherapp.domain.GetWeatherUseCase
import com.example.simpleweatherapp.domain.NavigationData
import com.example.simpleweatherapp.domain.WeatherViewState
import com.example.simpleweatherapp.domain.toViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing UI state and business logic for the weather app.
 *
 * This ViewModel:
 * - Fetches weather data for cities.
 * - Manages the list of saved cities.
 * - Handles navigation events using a sealed class with a single-event wrapper.
 * - Interacts with use cases for data operations and applies dependency injection via Hilt.
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val addCityUseCase: AddCityUseCase,
) : ViewModel() {

    /**
     * Single-event flow to handle navigation events in the app.
     * It emits a [NavigationState] wrapped in [SingleEvent] to ensure the event is consumed only once.
     */
    private val _navigationSingedEvent = MutableStateFlow<SingleEvent<NavigationState>?>(null)
    val navigationSingedEvent: StateFlow<SingleEvent<NavigationState>?> = _navigationSingedEvent

    /**
     * State flow to manage the list of saved cities.
     */
    private val _cities = MutableStateFlow<List<City>>(emptyList())
    val cities: StateFlow<List<City>> = _cities

    /**
     * Default list of cities to add if no saved cities exist in the data source.
     */
    private val defaultCities = listOf("London", "Paris", "Vienna")

    /**
     * Initializes the ViewModel by loading the saved cities.
     */
    init {
        loadCities()
    }

    /**
     * Handles navigation logic for a given city and navigation type.
     * Emits a [NavigationState] wrapped in [SingleEvent] to manage the state of navigation.
     *
     * @param city The [City] for which navigation is triggered.
     * @param navigationType The type of navigation (e.g., details or historical weather).
     */
    fun navigate(city: City, navigationType: NavigationType) {
        viewModelScope.launch {
            // Emit a loading state
            _navigationSingedEvent.value = SingleEvent(NavigationState.Loading)

            try {
                // Fetch weather data
                val weather = getWeatherUseCase(city.name).toViewState()

                // Emit a success state with navigation data
                _navigationSingedEvent.value = SingleEvent(
                    NavigationState.Success(
                        NavigationData(
                            navigationType = navigationType,
                            viewState = weather,
                            isLoading = false
                        )
                    )
                )
            } catch (e: Exception) {
                // Emit an error state with a descriptive message
                _navigationSingedEvent.value = SingleEvent(
                    NavigationState.Error(e.message ?: "An unexpected error occurred")
                )
            }
        }
    }

    /**
     * Loads the list of saved cities from the data source.
     * If the list is empty, adds the default cities.
     */
    private fun loadCities() {
        viewModelScope.launch {
            getCitiesUseCase.execute().collect { cityList ->
                if (cityList.isEmpty()) {
                    addDefaultCities()
                } else {
                    _cities.value = cityList
                }
            }
        }
    }

    /**
     * Adds the default cities to the data source.
     * This is invoked when no cities are found in the data source.
     */
    private fun addDefaultCities() {
        viewModelScope.launch {
            defaultCities.forEach { cityName ->
                addCityUseCase.execute(City(name = cityName))
            }
        }
    }

    /**
     * Adds a new city to the data source.
     *
     * @param name The name of the city to be added.
     */
    fun addCity(name: String) {
        viewModelScope.launch {
            addCityUseCase.execute(City(name = name))
        }
    }

    /**
     * Fetches weather data for a specific city.
     *
     * @param city The [City] for which weather data is fetched.
     * @return The weather data in a user-friendly [WeatherViewState] format, or null if an error occurs.
     */
    suspend fun fetchWeather(city: City): WeatherViewState? {
        return try {
            val weather = getWeatherUseCase(city.name)
            weather.toViewState()
        } catch (e: Exception) {
            handleError("Error fetching weather data: ${e.message}")
            null
        }
    }

    /**
     * Handles and logs errors that occur during data operations.
     *
     * @param message A descriptive error message.
     * @param throwable An optional [Throwable] representing the cause of the error.
     */
    private fun handleError(message: String, throwable: Throwable? = null) {
        val errorType = when (throwable) {
            is java.net.UnknownHostException -> "Network Error: Please check your internet connection."
            is java.net.SocketTimeoutException -> "Request Timed Out: Try again later."
            else -> message
        }
        println("Error: $errorType")
        throwable?.printStackTrace() // Log detailed stack trace if needed
    }

    /**
     * Represents the navigation state for the app.
     *
     * This sealed class defines the possible states:
     * - [Loading]: Indicates a loading state during navigation.
     * - [Success]: Indicates successful navigation with relevant data.
     * - [Error]: Indicates a navigation error with a descriptive message.
     * - [Idle]: Represents the default state when no operation is active.
     */
    sealed class NavigationState {
        object Loading : NavigationState()
        data class Success(val data: NavigationData) : NavigationState()
        data class Error(val message: String) : NavigationState()
        object Idle : NavigationState()
    }
}
