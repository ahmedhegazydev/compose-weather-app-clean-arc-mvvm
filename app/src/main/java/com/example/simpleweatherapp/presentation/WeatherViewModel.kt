package com.example.simpleweatherapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpleweatherapp.data.NavigationType
import com.example.simpleweatherapp.data.offline.City
import com.example.simpleweatherapp.domain.AddCityUseCase
import com.example.simpleweatherapp.domain.GetCitiesUseCase
import com.example.simpleweatherapp.domain.GetWeatherUseCase
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
 * This ViewModel interacts with use cases to fetch weather data, manage saved cities,
 * and handle navigation state. It is lifecycle-aware and integrates with Hilt for dependency injection.
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val addCityUseCase: AddCityUseCase,
) : ViewModel() {

    // Navigation state management
    private val _navigationType = MutableStateFlow<NavigationType>(NavigationType.NONE)
    val navigationType: StateFlow<NavigationType> = _navigationType

    /**
     * Updates the navigation type for the UI.
     *
     * @param type The new [NavigationType].
     */
    fun setNavigationType(type: NavigationType) {
        _navigationType.value = type
    }

    // City management
    private val _cities = MutableStateFlow<List<City>>(emptyList())
    val cities: StateFlow<List<City>> = _cities
    private val defaultCities = listOf("London", "Paris", "Vienna") // Default cities

    init {
        loadCities()
    }

    /**
     * Loads cities from the data source. If no cities are found, it adds default cities.
     */
    private fun loadCities() {
        viewModelScope.launch {
            getCitiesUseCase.execute().collect { cityList ->
                if (cityList.isEmpty()) {
                    addDefaultCities()
//                    _cities.value = defaultCities.map { City(name = it) } // Update `_cities` with default cities
                } else {
                    _cities.value = cityList
                }
            }
        }
    }

    /**
     * Adds default cities to the data source.
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
     * @param name The name of the city to add.
     */
    fun addCity(name: String) {
        viewModelScope.launch {
            addCityUseCase.execute(City(name = name))
        }
    }

    // Weather state management
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState

    /**
     * Fetches weather data for a specified city.
     *
     * @param city The [City] for which weather data is fetched.
     */
    fun fetchWeather(city: City) {
        viewModelScope.launch {
            try {
                val weather = getWeatherUseCase(city.name)
                val viewState = weather.toViewState()
                _weatherState.value = WeatherState.Success(viewState)
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    /**
     * Represents the possible states of the weather data.
     */
    sealed class WeatherState {
        /**
         * Represents a loading state where data is being fetched.
         */
        object Loading : WeatherState()

        /**
         * Represents a successful data fetch, containing a [WeatherViewState].
         *
         * @param viewState The user-friendly representation of the weather data.
         */
        data class Success(val viewState: WeatherViewState) : WeatherState()

        /**
         * Represents an error state with an associated error message.
         *
         * @param message A description of the error.
         */
        data class Error(val message: String) : WeatherState()
    }
}
