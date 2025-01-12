package com.example.simpleweatherapp.data.offline

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var cityDao: CityDao

    @Before
    fun setUp() {
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        cityDao = database.cityDao()
    }

    @After
    fun tearDown() {
        // Close the database after each test
        database.close()
    }

    @Test
    fun insertCityAndRetrieveByName() = runBlocking {
        val city = City(id = 1, name = "Cairo")
        cityDao.insertCity(city)

        val retrievedCity = cityDao.getCityByName("Cairo")
        assertEquals(city, retrievedCity)
    }

    @Test
    fun deleteCityRemovesItFromDatabase() = runBlocking {
        val city = City(id = 2, name = "Alexandria")
        cityDao.insertCity(city)

        cityDao.deleteCity(city)
        val retrievedCity = cityDao.getCityByName("Alexandria")
        assertEquals(null, retrievedCity)
    }

    @Test
    fun retrieveAllCities() = runBlocking {
        val city1 = City(id = 1, name = "Cairo")
        val city2 = City(id = 2, name = "Alexandria")
        cityDao.insertCity(city1)
        cityDao.insertCity(city2)

        val cities = cityDao.getAllCities()
//        assertEquals(2, cities.size)
        assertEquals(listOf(city1, city2), cities)
    }

    @Test
    fun updateCityUpdatesData() = runBlocking {
        val city = City(id = 1, name = "Cairo")
        cityDao.insertCity(city)

        val updatedCity = City(id = 1, name = "New Cairo")
        cityDao.updateCity(updatedCity)

        val retrievedCity = cityDao.getCityByName("New Cairo")
        assertEquals(updatedCity, retrievedCity)
    }
}
