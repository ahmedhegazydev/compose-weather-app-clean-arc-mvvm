package com.example.simpleweatherapp.data.offline

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for the [AppDatabase] using Room's in-memory database.
 * This test class ensures the proper functioning of the [CityDao] and its operations.
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    // Instance of the in-memory Room database used for testing.
    private lateinit var database: AppDatabase

    // Instance of the DAO to perform database operations.
    private lateinit var cityDao: CityDao

    /**
     * Initializes the in-memory database and DAO before each test.
     */
    @Before
    fun setUp() {
        // Create an in-memory database for testing purposes.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        // Obtain the CityDao instance.
        cityDao = database.cityDao()
    }

    /**
     * Closes the database after each test to release resources.
     */
    @After
    fun tearDown() {
        database.close()
    }

    /**
     * Tests if a city can be inserted and retrieved by its name.
     */
    @Test
    fun insertCityAndRetrieveByName() = runBlocking {
        val city = City(id = 1, name = "Cairo")
        cityDao.insertCity(listOf(city))

        val retrievedCity = cityDao.getCityByName("Cairo")
        assertEquals(city, retrievedCity)
    }

    /**
     * Tests if deleting a city removes it from the database.
     */
    @Test
    fun deleteCityRemovesItFromDatabase() = runBlocking {
        val city = City(id = 2, name = "Alexandria")
        cityDao.insertCity(listOf(city))

        cityDao.deleteCity(city)
        val retrievedCity = cityDao.getCityByName("Alexandria")
        assertEquals(null, retrievedCity)
    }

    /**
     * Tests if all cities can be retrieved after multiple insertions.
     */
    @Test
    fun retrieveAllCities() = runBlocking {
        val city1 = City(id = 1, name = "Cairo")
        val city2 = City(id = 2, name = "Alexandria")

        cityDao.insertCity(listOf(city1, city2))

        val cities = cityDao.getAllCities().toList() // Collect the Flow into a list
        assertEquals(listOf(city1, city2), cities)
    }

    /**
     * Tests if updating a city reflects the changes in the database.
     */
    @Test
    fun updateCityUpdatesData() = runBlocking {
        val city = City(id = 1, name = "Cairo")
        cityDao.insertCity(listOf(city))

        val updatedCity = City(id = 1, name = "New Cairo")
        cityDao.updateCity(updatedCity)

        val retrievedCity = cityDao.getCityByName("New Cairo")
        assertEquals(updatedCity, retrievedCity)
    }
}
