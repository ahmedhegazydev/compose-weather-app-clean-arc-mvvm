package com.example.simpleweatherapp.domain

import com.example.simpleweatherapp.data.offline.City
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving weather information for a specific city.
 *
 * @property repository The repository that handles weather data fetching.
 */
class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    /**
     * Invokes the use case to get weather information for the given city.
     *
     * @param city The name of the city to fetch weather data for.
     * @return A [Weather] object containing weather details for the specified city.
     */
    suspend operator fun invoke(city: String): Weather {
        return repository.getWeather(city)
    }
}

/**
 * Use case for retrieving a list of saved cities from the local database.
 *
 * @property repository The repository that handles city-related data.
 */
class GetCitiesUseCase @Inject constructor(private val repository: CityRepository) {
    /**
     * Executes the use case to fetch all saved cities.
     *
     * @return A [Flow] emitting a list of [City] objects.
     */
     fun execute(): Flow<List<City>> = repository.getCities()
}

/**
 * Use case for adding a city to the local database.
 *
 * @property repository The repository that handles city-related data.
 */
class AddCityUseCase @Inject constructor(private val repository: CityRepository) {
    /**
     * Executes the use case to add a new city.
     *
     * @param city The [City] object to be added to the database.
     */
    suspend fun execute(city: List<City>) = repository.addCity(city)
}
