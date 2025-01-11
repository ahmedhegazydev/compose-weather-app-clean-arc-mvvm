package com.example.simpleweatherapp.data

import com.google.gson.annotations.SerializedName

/**
 * Data transfer object representing weather data from the API.
 *
 * This class and its nested data classes map directly to the JSON structure returned by the weather API.
 */
data class WeatherDto(
    /**
     * Coordinates of the location.
     */
    @SerializedName("coord") val coordDto: CoordDto,

    /**
     * List of weather conditions.
     */
    @SerializedName("weather") val weather: List<WeatherDescDto>,

    /**
     * Internal parameter returned by the API.
     */
    @SerializedName("base") val base: String,

    /**
     * Main weather information, including temperature, pressure, and humidity.
     */
    @SerializedName("main") val mainDto: MainDto,

    /**
     * Visibility in meters.
     */
    @SerializedName("visibility") val visibility: Int,

    /**
     * Wind information.
     */
    @SerializedName("wind") val windDto: WindDto,

    /**
     * Cloud coverage information.
     */
    @SerializedName("clouds") val cloudsDto: CloudsDto,

    /**
     * Data calculation time (Unix timestamp).
     */
    @SerializedName("dt") val dt: Long,

    /**
     * System information including country and sunrise/sunset times.
     */
    @SerializedName("sys") val sysDto: SysDto,

    /**
     * Shift in seconds from UTC for the location.
     */
    @SerializedName("timezone") val timezone: Int,

    /**
     * City ID.
     */
    @SerializedName("id") val id: Int,

    /**
     * City name.
     */
    @SerializedName("name") val name: String,

    /**
     * HTTP response code for the request.
     */
    @SerializedName("cod") val cod: Int
)

/**
 * Data transfer object for geographic coordinates.
 */
data class CoordDto(
    /**
     * Longitude of the location.
     */
    @SerializedName("lon") val lon: Float,

    /**
     * Latitude of the location.
     */
    @SerializedName("lat") val lat: Float
)

/**
 * Data transfer object for weather description.
 */
data class WeatherDescDto(
    /**
     * Weather condition ID.
     */
    @SerializedName("id") val id: Int,

    /**
     * Group of weather parameters (e.g., Rain, Snow, Clear, Clouds).
     */
    @SerializedName("main") val main: String,

    /**
     * Detailed weather description.
     */
    @SerializedName("description") val description: String,

    /**
     * Weather icon ID for graphical representation.
     */
    @SerializedName("icon") val icon: String
)

/**
 * Data transfer object for main weather information.
 */
data class MainDto(
    /**
     * Current temperature (in Kelvin by default).
     */
    @SerializedName("temp") val temp: Double,

    /**
     * Feels-like temperature (in Kelvin by default).
     */
    @SerializedName("feels_like") val feelsLike: Double,

    /**
     * Minimum temperature at the moment.
     */
    @SerializedName("temp_min") val tempMin: Double,

    /**
     * Maximum temperature at the moment.
     */
    @SerializedName("temp_max") val tempMax: Double,

    /**
     * Atmospheric pressure at sea level (hPa).
     */
    @SerializedName("pressure") val pressure: Int,

    /**
     * Humidity percentage.
     */
    @SerializedName("humidity") val humidity: Int,

    /**
     * Atmospheric pressure on sea level (hPa) (optional).
     */
    @SerializedName("sea_level") val seaLevel: Int? = null,

    /**
     * Atmospheric pressure on ground level (hPa) (optional).
     */
    @SerializedName("grnd_level") val grndLevel: Int? = null
)

/**
 * Data transfer object for wind information.
 */
data class WindDto(
    /**
     * Wind speed (meters per second).
     */
    @SerializedName("speed") val speed: Double,

    /**
     * Wind direction in degrees.
     */
    @SerializedName("deg") val deg: Int,

    /**
     * Wind gust speed (optional).
     */
    @SerializedName("gust") val gust: Double? = null
)

/**
 * Data transfer object for cloud coverage information.
 */
data class CloudsDto(
    /**
     * Cloudiness percentage.
     */
    @SerializedName("all") val all: Int
)

/**
 * Data transfer object for system-related weather information.
 */
data class SysDto(
    /**
     * Internal parameter (type).
     */
    @SerializedName("type") val type: Int,

    /**
     * Internal parameter (ID).
     */
    @SerializedName("id") val id: Int,

    /**
     * Country code (e.g., US, GB).
     */
    @SerializedName("country") val country: String,

    /**
     * Sunrise time (Unix timestamp).
     */
    @SerializedName("sunrise") val sunrise: Long,

    /**
     * Sunset time (Unix timestamp).
     */
    @SerializedName("sunset") val sunset: Long
)

/**
 * Enum for different types of navigation in the application.
 */
enum class NavigationType {
    /**
     * Navigate to the details screen.
     */
    DETAILS,

    /**
     * Navigate to the historical weather screen.
     */
    HISTORICAL,

    /**
     * No navigation specified.
     */
    NONE
}
