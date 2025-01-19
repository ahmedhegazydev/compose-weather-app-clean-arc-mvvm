package com.example.simpleweatherapp.data

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: WeatherApiService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // MockWebServer's URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getWeather returns correct data`() = runBlocking {
        // Mock response from the API
        val mockResponse = MockResponse()
            .setBody(
                """
            {
  "coord": {
    "lon": -0.1257,
    "lat": 51.5085
  },
  "weather": [
    {
      "id": 800,
      "main": "Clear",
      "description": "clear sky",
      "icon": "01d"
    }
  ],
  "base": "stations",
  "main": {
    "temp": 298.15,
    "feels_like": 298.15,
    "temp_min": 296.15,
    "temp_max": 300.15,
    "pressure": 1013,
    "humidity": 40
  },
  "visibility": 10000,
  "wind": {
    "speed": 3.6,
    "deg": 90,
    "gust": 5.2
  },
  "clouds": {
    "all": 0
  },
  "dt": 1692658355,
  "sys": {
    "type": 1,
    "id": 1414,
    "country": "GB",
    "sunrise": 1692612800,
    "sunset": 1692660840
  },
  "timezone": 3600,
  "id": 2643743,
  "name": "London",
  "cod": 200
}

            """.trimIndent()
            )
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        // Perform the repository call
        // Perform the repository call
        val weatherDto = apiService.getWeather("London", "test_api_key").body()
        assertNotNull(weatherDto)

        val weather = weatherDto?.toDomain()
        assertNotNull(weather)

        // Assertions
        assertEquals("London", weather?.name)
        assertEquals(-0.1256999969482422, weather?.coord?.lon)
        assertEquals(51.50849914550781, weather?.coord?.lat)
        assertEquals(800, weather?.weatherDescriptions?.first()?.id)
        assertEquals("Clear", weather?.weatherDescriptions?.first()?.main)
        assertEquals("clear sky", weather?.weatherDescriptions?.first()?.description)
        assertEquals("01d", weather?.weatherDescriptions?.first()?.icon)
        assertEquals(298.15, weather?.main?.temp)
        assertEquals(298.15, weather?.main?.feelsLike)
        assertEquals(296.15, weather?.main?.tempMin)
        assertEquals(300.15, weather?.main?.tempMax)
        assertEquals(1013, weather?.main?.pressure)
        assertEquals(40, weather?.main?.humidity)
        assertEquals(10000, weather?.visibility)
        assertEquals(3.6, weather?.wind?.speed)
        assertEquals(90, weather?.wind?.deg)
        assertEquals(0, weather?.clouds?.all)
        assertEquals(1692658355, weather?.timestamp)
        assertEquals("GB", weather?.systemInfo?.country)
        assertEquals(1, weather?.systemInfo?.type)
        assertEquals(1414, weather?.systemInfo?.id)
        assertEquals(1692612800, weather?.systemInfo?.sunrise)
        assertEquals("GB", weather?.systemInfo?.country)
        assertEquals(1692660840, weather?.systemInfo?.sunset)
        assertEquals(3600, weather?.timezone)
        assertEquals(2643743, weather?.id)
        assertEquals(200, weather?.coord)

    }

    @Test
    fun getWeather_handles_error_response() {
        runBlocking {
            // Mock error response from the API
            val mockResponse = MockResponse()
                .setResponseCode(404)
                .setBody(
                    """
                    {
                        "cod": "404",
                        "message": "city not found"
                    }
                    """.trimIndent()
                )
            mockWebServer.enqueue(mockResponse)

            try {
                // Perform the API call
                apiService.getWeather("InvalidCity", "test_api_key")
            } catch (e: Exception) {
                // Assert exception message
                assertEquals(true, e.message?.contains("city not found") ?: false)
            }
        }
    }
}
