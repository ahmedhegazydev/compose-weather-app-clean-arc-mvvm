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

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(apiService: WeatherApiService): WeatherRepository {
        return WeatherRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideCityRepository(cityDao: CityDao): CityRepository {
        return CityRepositoryImpl(cityDao)
    }
}