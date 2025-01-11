package com.example.simpleweatherapp.presentation;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewModelScope;
import com.example.simpleweatherapp.Event;
import com.example.simpleweatherapp.data.NavigationType;
import com.example.simpleweatherapp.data.offline.City;
import com.example.simpleweatherapp.domain.AddCityUseCase;
import com.example.simpleweatherapp.domain.GetCitiesUseCase;
import com.example.simpleweatherapp.domain.GetWeatherUseCase;
import com.example.simpleweatherapp.domain.NavigationData;
import com.example.simpleweatherapp.domain.WeatherViewState;
import com.example.simpleweatherapp.domain.toViewState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.launch;
import javax.inject.Inject;

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

    private val _navigationEvent = MutableStateFlow<Event<NavigationData>?>(null);
    val navigationEvent: StateFlow<Event<NavigationData>?> = _navigationEvent;

    fun navigateToDetails(city: City) {
        viewModelScope.launch {
            val result = fetchWeather(city)
            if (result != null) {
                val data = NavigationData(
                    navigationType = NavigationType.DETAILS,
                    viewState = result
                );
                _navigationEvent.value = Event(data)
            } else {
                handleError("Failed to navigate to details");
            }
        }
    }

    fun navigateToHistorical(city: City) {
        viewModelScope.launch {
            val result = fetchWeather(city);
            if (result != null) {
                val data = NavigationData(
                    navigationType = NavigationType.HISTORICAL,
                    viewState = result
                );
                _navigationEvent.value = Event(data)
            } else {
                handleError("Failed to navigate to historical data");
            }
        }
    }

    private val _cities = MutableStateFlow<List<City>>(emptyList());
    val cities: StateFlow<List<City>> = _cities;
    private val defaultCities = listOf("London", "Paris", "Vienna");

    init {
        loadCities();
    }

    /**
     * Loads cities from the data source. If no cities are found, it adds default cities.
     */
    private fun loadCities() {
        viewModelScope.launch {
            getCitiesUseCase.execute().collect { cityList ->
                if (cityList.isEmpty()) {
                    addDefaultCities();
                } else {
                    _cities.value = cityList;
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
                addCityUseCase.execute(City(name = cityName));
            };
        }
    }

    /**
     * Adds a new city to the data source.
     *
     * @param name The name of the city to add.
     */
    fun addCity(name: String) {
        viewModelScope.launch {
            addCityUseCase.execute(City(name = name));
        }
    }

    /**
     * Fetches weather data for a specified city.
     *
     * @param city The [City] for which weather data is fetched.
     */
    suspend fun fetchWeather(city: City): WeatherViewState? {
        return try {
            val weather = getWeatherUseCase(city.name);
            weather.toViewState();
        } catch (e: Exception) {
            handleError("Error fetching weather data: ${e.message}");
            null
        }
    }

    /**
     * Handles errors by logging or displaying a message.
     *
     * @param message The error message to handle.
     */
    private fun handleError(message: String) {
        println(message);
    }

    /**
     * Represents the possible states of the weather data.
     */
    sealed class WeatherState {
        /**
         * Represents a loading state where data is being fetched.
         */
        object Loading : WeatherState();

        /**
         * Represents a successful data fetch, containing a [WeatherViewState].
         *
         * @param viewState The user-friendly representation of the weather data.
         */
        data class Success(val viewState: WeatherViewState) : WeatherState();

        /**
         * Represents an error state with an associated error message.
         *
         * @param message A description of the error.
         */
        data class Error(val message: String) : WeatherState();
    }
}
