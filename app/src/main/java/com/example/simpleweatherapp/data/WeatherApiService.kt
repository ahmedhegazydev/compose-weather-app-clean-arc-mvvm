package com.example.simpleweatherapp.data

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface defining API calls to fetch weather data using Retrofit.
 */
interface WeatherApiService {

    /**
     * Fetches current weather data for a specified city.
     *
     * @param city The name of the city for which to fetch weather information.
     * @param apiKey The API key used for authenticating requests to the OpenWeather API.
     * @return A [WeatherDto] object containing the weather data for the specified city.
     */
    @GET("/data/2.5/weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
    ): WeatherDto
}
