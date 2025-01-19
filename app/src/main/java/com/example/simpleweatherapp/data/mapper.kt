package com.example.simpleweatherapp.data

import com.example.simpleweatherapp.domain.*

/**
 * Extension function to map [WeatherDto] to its domain model [Weather].
 *
 * This function transforms the data transfer object (DTO) received from the API
 * into the corresponding domain object used within the application.
 *
 * @return A [Weather] object with data mapped from [WeatherDto].
 */
fun WeatherDto.toDomain(): Weather {
    return Weather(
        coord = Coord(coordDto.lon.toDouble(), coordDto.lat.toDouble()),
        weatherDescriptions = weather.map { it.toDomain() },
        base = base,
        main = mainDto.toDomain(),
        visibility = visibility,
        wind = windDto.toDomain(),
        clouds = cloudsDto.toDomain(),
        timestamp = dt,
        systemInfo = sysDto.toDomain(),
        timezone = timezone,
        id = id,
        name = name,
        statusCode = cod
    )
}

/**
 * Extension function to map [WeatherDescDto] to its domain model [WeatherDescription].
 *
 * @return A [WeatherDescription] object with data mapped from [WeatherDescDto].
 */
fun WeatherDescDto.toDomain() = WeatherDescription(id, main, description, icon)

/**
 * Extension function to map [MainDto] to its domain model [Main].
 *
 * @return A [Main] object with data mapped from [MainDto].
 */
fun MainDto.toDomain() = Main(temp, feelsLike, tempMin, tempMax, pressure, humidity)

/**
 * Extension function to map [WindDto] to its domain model [Wind].
 *
 * @return A [Wind] object with data mapped from [WindDto].
 */
fun WindDto.toDomain() = Wind(speed, deg)

/**
 * Extension function to map [CloudsDto] to its domain model [Clouds].
 *
 * @return A [Clouds] object with data mapped from [CloudsDto].
 */
fun CloudsDto.toDomain() = Clouds(all)

/**
 * Extension function to map [SysDto] to its domain model [SystemInfo].
 *
 * @return A [SystemInfo] object with data mapped from [SysDto].
 */
fun SysDto.toDomain() = SystemInfo(
    type,
    id,
    country, sunrise, sunset)
