package com.example.simpleweatherapp.data


import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepositoryImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: WeatherApiService
    private lateinit var weatherRepository: WeatherRepositoryImpl

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)

        weatherRepository = WeatherRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getWeather returns mapped Weather object`() = runBlocking {
        // Mock response from the API
        val mockResponse = MockResponse()
            .setBody(
                """
                {
                    "coord": {"lon": 139, "lat": 35},
                    "weather": [{"id": 800, "main": "Clear", "description": "clear sky", "icon": "01d"}],
                    "base": "stations",
                    "main": {"temp": 293.25, "feels_like": 293.51, "temp_min": 293.15, "temp_max": 293.35, "pressure": 1013, "humidity": 53},
                    "visibility": 10000,
                    "wind": {"speed": 3.6, "deg": 220},
                    "clouds": {"all": 1},
                    "dt": 1560350645,
                    "sys": {"type": 1, "id": 8074, "country": "JP", "sunrise": 1560281377, "sunset": 1560333478},
                    "timezone": 32400,
                    "id": 1851632,
                    "name": "Shuzenji",
                    "cod": 200
                }
                """.trimIndent()
            )
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        // Perform the API call
        val weather = weatherRepository.getWeather("Shuzenji")

        // Assertions
        assertEquals("Shuzenji", weather.name)
        assertEquals(139.0, weather.coord.lon)
        assertEquals(35.0, weather.coord.lat)
        assertEquals("Clear", weather.weatherDescriptions.first().main)
        assertEquals(293.25, weather.main.temp)
    }

    @Test
    fun `getWeather throws exception on API error`() = runBlocking {
        // Mock error response from the API
        val mockResponse = MockResponse()
            .setResponseCode(500)
            .setBody("Internal Server Error") // Simulate server error response body
        mockWebServer.enqueue(mockResponse)

        try {
            // Perform the API call
            weatherRepository.getWeather("InvalidCity")
            // Fail if no exception is thrown
            assert(false) { "Expected an exception but none was thrown." }
        } catch (e: Exception) {
            // Assert the exception message
            assertEquals("API error: Internal Server Error", e.message)
        }
    }

}
