package com.example.simpleweatherapp.domain

import com.example.simpleweatherapp.data.offline.City
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for weather-related data operations.
 *
 * This interface defines the contract for fetching weather data
 * from remote or local sources.
 */
interface WeatherRepository {
    /**
     * Fetches weather information for a specific city.
     *
     * @param city The name of the city to retrieve weather data for.
     * @return A [Weather] object containing weather details for the specified city.
     */
    suspend fun getWeather(city: String): Weather
}

/**
 * Repository interface for managing city-related data operations.
 *
 * This interface defines the contract for retrieving and saving city information
 * to local or remote sources.
 */
interface CityRepository {
    /**
     * Retrieves a list of all saved cities.
     *
     * @return A [Flow] emitting a list of [City] objects representing the saved cities.
     */
     fun getCities(): Flow<List<City>>

    /**
     * Adds a new city to the data source.
     *
     * @param city The [City] object to be added.
     */
    suspend fun addCity(city: List<City>)
}
