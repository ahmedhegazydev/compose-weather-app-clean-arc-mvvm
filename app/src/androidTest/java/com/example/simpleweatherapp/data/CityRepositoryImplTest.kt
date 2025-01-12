package com.example.simpleweatherapp.data.offline

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.simpleweatherapp.data.CityRepositoryImpl
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CityRepositoryImplTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: CityDao
    private lateinit var cityRepository: CityRepositoryImpl

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.cityDao()
        cityRepository = CityRepositoryImpl(dao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addCityAndRetrieveWorksCorrectly() = runBlocking {
        // Given
        val city = City(name = "New York")

        // When
        cityRepository.addCity(city)
        val cities = cityRepository.getCities().first()

        // Then
        assertEquals(1, cities.size)
        assertEquals("New York", cities[0].name)
    }

    @Test
    fun getCitiesReturnsEmptyListInitially() = runBlocking {
        // When
        val cities = cityRepository.getCities().first()

        // Then
        assertEquals(0, cities.size)
    }

//    @Test
//    fun addCityIgnoresDuplicates() = runBlocking {
//        // Given
//        val city = City(name = "Los Angeles")
//
//        // When
//        cityRepository.addCity(city)
//        cityRepository.addCity(city) // Add the same city again
//        val cities = cityRepository.getCities().first()
//
//        // Then
//        assertEquals(1, cities.size) // Duplicate should be ignored
//    }
}
