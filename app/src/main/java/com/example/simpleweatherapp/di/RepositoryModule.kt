package com.example.simpleweatherapp.di

import com.example.simpleweatherapp.data.CityRepositoryImpl
import com.example.simpleweatherapp.data.WeatherApiService
import com.example.simpleweatherapp.data.WeatherRepositoryImpl
import com.example.simpleweatherapp.data.offline.CityDao
import com.example.simpleweatherapp.domain.CityRepository
import com.example.simpleweatherapp.domain.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * A Dagger module responsible for providing repository dependencies for the application.
 * This module binds implementations of repositories to their corresponding interfaces
 * and ensures these dependencies are available as singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Provides a singleton instance of the [WeatherRepository].
     *
     * @param apiService The [WeatherApiService] used to make network calls for weather data.
     * @return An implementation of [WeatherRepository] ([WeatherRepositoryImpl]).
     */
    @Provides
    @Singleton
    fun provideWeatherRepository(apiService: WeatherApiService): WeatherRepository {
        return WeatherRepositoryImpl(apiService)
    }

    /**
     * Provides a singleton instance of the [CityRepository].
     *
     * @param cityDao The [CityDao] used for accessing city data from the local database.
     * @return An implementation of [CityRepository] ([CityRepositoryImpl]).
     */
    @Provides
    @Singleton
    fun provideCityRepository(cityDao: CityDao): CityRepository {
        return CityRepositoryImpl(cityDao)
    }
}
