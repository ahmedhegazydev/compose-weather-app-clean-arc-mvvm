package com.example.simpleweatherapp.data

import com.example.simpleweatherapp.BuildConfig
import com.example.simpleweatherapp.data.offline.City
import com.example.simpleweatherapp.data.offline.CityDao
import com.example.simpleweatherapp.domain.CityRepository
import com.example.simpleweatherapp.domain.Weather
import com.example.simpleweatherapp.domain.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of the [WeatherRepository] interface.
 *
 * This class provides weather data by fetching it from a remote API using [WeatherApiService].
 *
 * @property apiService The service interface for making API calls to retrieve weather data.
 */
class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
) : WeatherRepository {

    /**
     * Retrieves weather data for a given city.
     *
     * This method makes an API call to fetch the weather data for the specified city,
     * using the API key from [API_KEY].
     *
     * @param city The name of the city to retrieve weather data for.
     * @return A [Weather] object containing the weather details of the specified city.
     * @throws Exception If the API call fails.
     */
    override suspend fun getWeather(city: String): Weather {
        val response = apiService.getWeather(city, BuildConfig.API_KEY)

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return body.toDomain() // Map WeatherDto to Weather
            } else {
                throw Exception("API response body is null")
            }
        } else {
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("API error: $errorMessage")
        }
    }

}

/**
 * Implementation of the [CityRepository] interface.
 *
 * This class provides methods for managing city data stored locally using [CityDao].
 *
 * @property cityDao The DAO interface for accessing and modifying city data in the local database.
 */
class CityRepositoryImpl @Inject constructor(
    private val cityDao: CityDao,
) : CityRepository {

    /**
     * Retrieves a list of all cities stored in the local database.
     *
     * This method uses the [CityDao] to fetch the data and returns a reactive [Flow]
     * that emits updates whenever the data changes.
     *
     * @return A [Flow] emitting a list of [City] objects.
     */
    override fun getCities(): Flow<List<City>> = cityDao.getAllCities()

    /**
     * Adds a new city to the local database.
     *
     * This method inserts a new city into the `cities` table using [CityDao].
     * If the city already exists, it is ignored due to the conflict strategy in the DAO.
     *
     * @param city The [City] object to be added.
     */
    override suspend fun addCity(city: List<City>) = cityDao.insertCity(city)
//    override suspend fun addCity(cityList: List<City>) {
//        if (cityList.isEmpty()) {
//            throw IllegalArgumentException("City list cannot be empty")
//        }
//
//        val uniqueCities = cityList.distinctBy { it.name } // Remove duplicates by name
//        if (uniqueCities.isEmpty()) {
//            throw IllegalArgumentException("City list contains only duplicates")
//        }
//
//        // Batch processing
////        Performance:
////        Reduces the load on the database by breaking large transactions into smaller, manageable chunks.
////        Prevents long transaction times and potential database locks.
//        uniqueCities.chunked(100).forEach { batch ->
//            cityDao.insertCity(batch)
//        }
//    }


}


