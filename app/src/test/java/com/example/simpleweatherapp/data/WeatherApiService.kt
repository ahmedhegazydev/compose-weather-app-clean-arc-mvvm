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
    fun `getWeather returns correct data`() {
        runBlocking {
            // Mock response from the API
            val mockResponse = MockResponse()
                .setBody(
                    """
                    {
                        "weather": [{"description": "clear sky"}],
                        "main": {"temp": 298.15, "humidity": 40},
                        "wind": {"speed": 3.6},
                        "name": "London"
                    }
                    """.trimIndent()
                )
                .setResponseCode(200)
            mockWebServer.enqueue(mockResponse)

            // Perform the API call
            val response = apiService.getWeather("London", "test_api_key")

            // Assertions
            assertNotNull(response)
            assertEquals("London", response.name)
            assertEquals("clear sky", response.weather.first().description)
        }
    }

//    @Test
//    fun getWeather_handles_error_response() {
//        runBlocking {
//            // Mock error response from the API
//            val mockResponse = MockResponse()
//                .setResponseCode(404)
//                .setBody(
//                    """
//                    {
//                        "cod": "404",
//                        "message": "city not found"
//                    }
//                    """.trimIndent()
//                )
//            mockWebServer.enqueue(mockResponse)
//
//            try {
//                // Perform the API call
//                apiService.getWeather("InvalidCity", "test_api_key")
//            } catch (e: Exception) {
//                // Assert exception message
//                assertEquals(true, e.message?.contains("city not found") ?: false)
//            }
//        }
//    }
}
