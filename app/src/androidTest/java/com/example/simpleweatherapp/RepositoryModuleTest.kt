package com.example.simpleweatherapp

import com.example.simpleweatherapp.data.WeatherApiService
import com.example.simpleweatherapp.data.WeatherRepositoryImpl
import com.example.simpleweatherapp.data.offline.CityDao
import com.example.simpleweatherapp.domain.CityRepository
import com.example.simpleweatherapp.domain.WeatherRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@HiltAndroidTest
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class RepositoryModuleTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var mockWebServer: MockWebServer

    @Inject
    lateinit var weatherRepository: WeatherRepository

    @Inject
    lateinit var cityRepository: CityRepository

    @Inject
    lateinit var cityDao: CityDao

    @Inject
    lateinit var apiService: WeatherApiService

    @Before
    fun setUp() {
        hiltRule.inject()
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testWeatherRepositoryIsProvidedCorrectly() {
        Assert.assertNotNull(weatherRepository)
        Assert.assertTrue(weatherRepository is WeatherRepository)
    }

    @Test
    fun testCityRepositoryIsProvidedCorrectly() {
        Assert.assertNotNull(cityRepository)
        Assert.assertTrue(cityRepository is CityRepository)
    }

    @Test
    fun testCityDaoIsProvidedCorrectly() {
        Assert.assertNotNull(cityDao)
    }

    @Test
    fun testWeatherApiServiceIsProvidedCorrectly() {
        Assert.assertNotNull(apiService)
        Assert.assertTrue(apiService is WeatherApiService)
    }

    @Test
    fun testWeatherRepositoryRetrievesData() = runBlocking {
        // Mock Response
        val mockResponse = """
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

        mockWebServer.enqueue(MockResponse().setBody(mockResponse).setResponseCode(200))

        // Update Retrofit baseUrl
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val testApiService = retrofit.create(WeatherApiService::class.java)
        val testWeatherRepository = WeatherRepositoryImpl(testApiService)

        val weather = testWeatherRepository.getWeather("Shuzenji")

        Assert.assertEquals("Shuzenji", weather.name)
        Assert.assertEquals(139.0, weather.coord.lon, 0.0)
        Assert.assertEquals(35.0, weather.coord.lat, 0.0)
    }
}
