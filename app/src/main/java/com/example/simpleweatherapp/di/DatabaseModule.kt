package com.example.simpleweatherapp.di

import android.content.Context
import androidx.room.Room
import com.example.simpleweatherapp.data.offline.AppDatabase
import com.example.simpleweatherapp.data.offline.CityDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger module responsible for providing database-related dependencies.
 *
 * This module is installed in the [SingletonComponent], meaning the provided
 * dependencies will have a singleton lifecycle within the application's scope.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides a singleton instance of [AppDatabase].
     *
     * This function creates a Room database named "city-database" for storing city-related data.
     *
     * @param context The application context provided by Dagger using the [ApplicationContext] qualifier.
     * @return A singleton instance of [AppDatabase].
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "city-database").build()
    }

    /**
     * Provides the [CityDao] instance for accessing city data from the database.
     *
     * The DAO is retrieved from the [AppDatabase] instance provided earlier.
     *
     * @param db The [AppDatabase] instance.
     * @return The [CityDao] instance for performing database operations on the "cities" table.
     */
    @Provides
    fun provideCityDao(db: AppDatabase): CityDao = db.cityDao()
}
