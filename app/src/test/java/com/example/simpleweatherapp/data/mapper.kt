package com.example.simpleweatherapp.data

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherDtoMappingTest {

    @Test
    fun `WeatherDto toDomain maps correctly`() {
        // Given
        val weatherDto = WeatherDto(
            coordDto = CoordDto(lon = 10.0f, lat = 20.0f),
            weather = listOf(WeatherDescDto(id = 1, main = "Clear", description = "clear sky", icon = "01d")),
            base = "stations",
            mainDto = MainDto(temp = 298.15, feelsLike = 299.15, tempMin = 296.15, tempMax = 300.15, pressure = 1013, humidity = 40),
            visibility = 10000,
            windDto = WindDto(speed = 5.0, deg = 180),
            cloudsDto = CloudsDto(all = 10),
            dt = 1630348156,
            sysDto = SysDto(
                type = 1,
                id = 123,
                country = "US", sunrise = 1630305600, sunset = 1630354800),
            timezone = -25200,
            id = 5391959,
            name = "San Francisco",
            cod = 200
        )

        // When
        val domain = weatherDto.toDomain()

        // Then
        assertEquals(10.0, domain.coord.lon, 0.0)
        assertEquals(20.0, domain.coord.lat, 0.0)
        assertEquals(1, domain.weatherDescriptions.first().id)
        assertEquals("Clear", domain.weatherDescriptions.first().main)
        assertEquals("clear sky", domain.weatherDescriptions.first().description)
        assertEquals("01d", domain.weatherDescriptions.first().icon)
        assertEquals("stations", domain.base)
        assertEquals(298.15, domain.main.temp, 0.0)
        assertEquals(299.15, domain.main.feelsLike, 0.0)
        assertEquals(296.15, domain.main.tempMin, 0.0)
        assertEquals(300.15, domain.main.tempMax, 0.0)
        assertEquals(1013, domain.main.pressure)
        assertEquals(40, domain.main.humidity)
        assertEquals(10000, domain.visibility)
        assertEquals(5.0, domain.wind.speed, 0.0)
        assertEquals(180, domain.wind.deg)
        assertEquals(10, domain.clouds.all)
        assertEquals(1630348156, domain.timestamp)
        assertEquals("US", domain.systemInfo.country)
        assertEquals(1630305600, domain.systemInfo.sunrise)
        assertEquals(1630354800, domain.systemInfo.sunset)
        assertEquals(-25200, domain.timezone)
        assertEquals(5391959, domain.id)
        assertEquals("San Francisco", domain.name)
        assertEquals(200, domain.statusCode)
    }

    @Test
    fun `WeatherDescDto toDomain maps correctly`() {
        // Given
        val dto = WeatherDescDto(id = 1, main = "Rain", description = "light rain", icon = "10d")

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(1, domain.id)
        assertEquals("Rain", domain.main)
        assertEquals("light rain", domain.description)
        assertEquals("10d", domain.icon)
    }

    @Test
    fun `MainDto toDomain maps correctly`() {
        // Given
        val dto = MainDto(temp = 293.15, feelsLike = 294.15, tempMin = 291.15, tempMax = 295.15, pressure = 1015, humidity = 50)

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(293.15, domain.temp, 0.0)
        assertEquals(294.15, domain.feelsLike, 0.0)
        assertEquals(291.15, domain.tempMin, 0.0)
        assertEquals(295.15, domain.tempMax, 0.0)
        assertEquals(1015, domain.pressure)
        assertEquals(50, domain.humidity)
    }

    @Test
    fun `WindDto toDomain maps correctly`() {
        // Given
        val dto = WindDto(speed = 7.5, deg = 270)

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(7.5, domain.speed, 0.0)
        assertEquals(270, domain.deg)
    }

    @Test
    fun `CloudsDto toDomain maps correctly`() {
        // Given
        val dto = CloudsDto(all = 75)

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(75, domain.all)
    }

    @Test
    fun `SysDto toDomain maps correctly`() {
        // Given
        val dto = SysDto(
            type = 1,
            id = 123,
            country = "GB", sunrise = 1630310400, sunset = 1630353600)

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals("GB", domain.country)
        assertEquals(1630310400, domain.sunrise)
        assertEquals(1630353600, domain.sunset)
    }
}
