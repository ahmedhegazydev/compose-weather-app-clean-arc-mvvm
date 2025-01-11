package com.example.simpleweatherapp.di

import com.example.simpleweatherapp.BuildConfig
import com.example.simpleweatherapp.data.WeatherApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing network-related dependencies.
 * This module is responsible for creating and configuring instances of
 * Retrofit and WeatherApiService that are used throughout the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides a singleton instance of Retrofit for making HTTP requests.
     *
     * @return A configured Retrofit instance with a logging interceptor
     * and a JSON converter factory.
     */
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        // Create a logging interceptor for debugging HTTP requests and responses
        val logging = HttpLoggingInterceptor().apply {
            // Set logging level to BODY to log the complete request and response data
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Build an OkHttpClient and add the logging interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        // Create and configure the Retrofit instance
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL) // Base URL for the API
            .addConverterFactory(GsonConverterFactory.create()) // Convert JSON to Kotlin objects
            .client(client) // Attach the OkHttpClient
            .build()
    }

    /**
     * Provides a singleton instance of WeatherApiService, which defines the API endpoints.
     *
     * @param retrofit The Retrofit instance used to create the service implementation.
     * @return An implementation of WeatherApiService.
     */
    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService {
        // Use Retrofit to create the API service implementation
        return retrofit.create(WeatherApiService::class.java)
    }
}
