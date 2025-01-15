package com.example.simpleweatherapp.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.simpleweatherapp.data.offline.AppDatabase
import com.example.simpleweatherapp.data.offline.City
import com.example.simpleweatherapp.data.offline.CityDao
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for the [CityRepositoryImpl] class.
 *
 * This test class ensures that the [CityRepositoryImpl] behaves correctly
 * when interacting with the database through [CityDao].
 */
@RunWith(AndroidJUnit4::class)
class CityRepositoryImplTest {

    // In-memory database for testing
    private lateinit var database: AppDatabase

    // DAO instance to interact with the database
    private lateinit var dao: CityDao

    // Repository being tested
    private lateinit var cityRepository: CityRepositoryImpl

    /**
     * Sets up the test environment by creating an in-memory Room database,
     * initializing the DAO and the repository.
     */
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.cityDao()
        cityRepository = CityRepositoryImpl(dao)
    }

    /**
     * Cleans up resources and closes the database after each test.
     */
    @After
    fun tearDown() {
        database.close()
    }

    /**
     * Tests that adding a city to the repository and retrieving it works correctly.
     */
    @Test
    fun addCityAndRetrieveWorksCorrectly() = runBlocking {
        // Given: A new city to add
        val city = City(name = "New York")

        // When: The city is added and retrieved
        cityRepository.addCity(city)
        val cities = cityRepository.getCities().first()

        // Then: Verify that the city is added and retrieved correctly
        assertEquals(1, cities.size)
        assertEquals("New York", cities[0].name)
    }

    /**
     * Tests that the repository initially returns an empty list of cities.
     */
    @Test
    fun getCitiesReturnsEmptyListInitially() = runBlocking {
        // When: Retrieving cities without adding any
        val cities = cityRepository.getCities().first()

        // Then: Verify that the list of cities is empty
        assertEquals(0, cities.size)
    }

    /**
     * Tests that adding a duplicate city is ignored by the repository.
     */
    @Test
    fun addCityIgnoresDuplicates() = runBlocking {
        // Given: A city to add
        val city = City(name = "Los Angeles")

        // When: The same city is added twice
        cityRepository.addCity(city)
        cityRepository.addCity(city) // Add duplicate
        val cities = cityRepository.getCities().first()

        // Then: Verify that duplicates are ignored
        assertEquals(1, cities.size) // Only one entry should exist
    }
}
