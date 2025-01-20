package com.example.simpleweatherapp.di

import com.example.simpleweatherapp.BuildConfig
import com.example.simpleweatherapp.data.WeatherApiService
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import javax.inject.Inject

@HiltAndroidTest
class NetworkModuleTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var retrofit: Retrofit

    @Inject
    lateinit var weatherApiService: WeatherApiService

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testRetrofitIsProvidedCorrectly() {
        // Assert that Retrofit is not null and configured correctly
        assertNotNull(retrofit)
        assert(retrofit.baseUrl().toString().contains(BuildConfig.BASE_URL))
    }

    @Test
    fun testWeatherApiServiceIsProvidedCorrectly() {
        // Assert that WeatherApiService is not null and provided
        assertNotNull(weatherApiService)
    }

    @Test
    fun testRetrofitConfigWithLoggingInterceptor() {
        // Assert that the Retrofit instance is using OkHttpClient with a logging interceptor
        val client = retrofit.callFactory() as OkHttpClient
        val interceptor = client.interceptors.find { it is HttpLoggingInterceptor }
        assertNotNull(interceptor)
        assert((interceptor as HttpLoggingInterceptor).level == HttpLoggingInterceptor.Level.BODY)
    }

    @Test
    fun testWeatherApiServiceFunctionality() = runBlocking {
        // Mock a request to ensure WeatherApiService works correctly (requires a valid API)
        val response = weatherApiService.getWeather("London", BuildConfig.API_KEY).body()
        assertNotNull(response)
        assertNotNull(response?.name)
        assert(response?.name?.contains("London") == true)
    }
}
