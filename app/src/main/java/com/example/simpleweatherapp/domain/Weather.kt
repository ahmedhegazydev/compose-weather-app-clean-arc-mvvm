package com.example.simpleweatherapp.domain

import com.example.simpleweatherapp.data.NavigationType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class representing weather information for a specific location.
 *
 * @property coord Geographical coordinates of the location.
 * @property weatherDescriptions List of weather conditions and descriptions.
 * @property base Internal parameter for OpenWeatherMap API.
 * @property main Main weather data such as temperature, pressure, and humidity.
 * @property visibility Visibility in meters.
 * @property wind Wind information including speed and direction.
 * @property clouds Cloudiness percentage.
 * @property timestamp Time of data calculation in Unix timestamp format.
 * @property systemInfo System-related information such as country and sunrise/sunset times.
 * @property timezone Shift in seconds from UTC for the location.
 * @property id Internal ID of the location.
 * @property name Name of the location (e.g., city name).
 * @property statusCode HTTP status code returned by the weather API.
 */
data class Weather(
    val coord: Coord,
    val weatherDescriptions: List<WeatherDescription>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val timestamp: Long,
    val systemInfo: SystemInfo,
    val timezone: Int,
    val id: Int,
    val name: String,
    val statusCode: Int,
)

/**
 * Data class for geographical coordinates.
 *
 * @property lon Longitude of the location.
 * @property lat Latitude of the location.
 */
data class Coord(val lon: Double, val lat: Double)

/**
 * Data class representing a single weather condition description.
 *
 * @property id ID of the weather condition.
 * @property main Main group of weather conditions (e.g., Rain, Snow).
 * @property description Detailed description of the weather.
 * @property icon Icon ID for weather representation.
 */
data class WeatherDescription(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
)

/**
 * Data class for main weather details.
 *
 * @property temp Current temperature in Kelvin.
 * @property feelsLike Temperature that it feels like in Kelvin.
 * @property tempMin Minimum temperature recorded in Kelvin.
 * @property tempMax Maximum temperature recorded in Kelvin.
 * @property pressure Atmospheric pressure in hPa.
 * @property humidity Humidity percentage.
 */
data class Main(
    val temp: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
)

/**
 * Data class for wind information.
 *
 * @property speed Wind speed in meters per second.
 * @property deg Wind direction in degrees.
 */
data class Wind(val speed: Double, val deg: Int)

/**
 * Data class for cloudiness information.
 *
 * @property all Cloudiness percentage.
 */
data class Clouds(val all: Int)

/**
 * Data class for system-related weather information.
 *
 * @property country Country code (e.g., "US" for the United States).
 * @property sunrise Sunrise time in Unix timestamp format.
 * @property sunset Sunset time in Unix timestamp format.
 */
data class SystemInfo(val country: String, val sunrise: Long, val sunset: Long)

/**
 * Data class representing a formatted and user-friendly view of weather information.
 *
 * @property description Weather description (e.g., "clear sky").
 * @property temperature Temperature in Celsius with "°C" suffix.
 * @property humidity Humidity percentage with "%" suffix.
 * @property windSpeed Wind speed in kilometers per hour with "km/h" suffix.
 * @property iconUrl URL for fetching the weather icon.
 * @property cityName Name of the city or location.
 * @property timestamp Formatted timestamp for the weather data (e.g., "dd.MM.yyyy - HH:mm").
 * @property location Geographical coordinates in string format.
 * @property weathers List of weather descriptions.
 */
data class WeatherViewState(
    val description: String,
    val temperature: String,
    val humidity: String,
    val windSpeed: String,
    val iconUrl: String,
    val cityName: String,
    val timestamp: String,
    val location: String,
    val weathers: List<WeatherDescription>,
)

/**
 * Extension function to convert a [Weather] object into a [WeatherViewState].
 *
 * @receiver [Weather] The weather data object to be converted.
 * @return A user-friendly representation of the weather data as [WeatherViewState].
 */
fun Weather.toViewState(): WeatherViewState {
    return WeatherViewState(
        description = weatherDescriptions.firstOrNull()?.description ?: "N/A",
        temperature = "${(main.temp - 273.15).toInt()}",
        humidity = "${main.humidity}",
        windSpeed = "${wind.speed}",
        //https://openweathermap.org/weather-conditions
        iconUrl = "https://openweathermap.org/img/wn/${weatherDescriptions.firstOrNull()?.icon ?: "01d"}" +
                "@2x.png",
        cityName = name,
        timestamp = SimpleDateFormat("dd.MM.yyyy - HH:mm", Locale.getDefault()).format(
            Date(
                timestamp * 1000
            )
        ),
        location = "Lat: ${coord.lat}, Lon: ${coord.lon}",
        weathers = weatherDescriptions
    )
}


/**
 * Data class used to manage navigation between different screens in the app.
 *
 * @property navigationType The type of navigation (e.g., DETAILS or HISTORICAL).
 * @property viewState Optional weather view state to pass to the target screen.
 */
data class NavigationData(
    val navigationType: NavigationType,
    val viewState: WeatherViewState? = null
)
