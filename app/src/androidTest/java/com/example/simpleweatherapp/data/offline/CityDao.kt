package com.example.simpleweatherapp.data.offline

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for the [CityDao] using an in-memory Room database.
 * This class validates the behavior of DAO operations such as inserting and retrieving data.
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//@SmallTest // Indicates that this test belongs to the "small" category (unit tests)
class CityDaoTest {

    /**
     * Rule to allow Room database interactions on the main thread during testing.
     * Ensures LiveData and other asynchronous mechanisms run immediately.
     */
//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase // In-memory database instance for testing
    private lateinit var dao: CityDao // DAO instance to interact with the database

    /**
     * Sets up the in-memory Room database and initializes the DAO before each test.
     */
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries() // Allows queries to run on the main thread for testing purposes
            .build()
        dao = database.cityDao() // Obtain an instance of CityDao
    }

    /**
     * Cleans up resources and closes the database after each test.
     */
    @After
    fun tearDown() {
        database.close()
    }

    /**
     * Tests the insertion of a city and verifies that the city can be retrieved.
     */
    @Test
    fun insertCity_and_getAllCities() = runBlocking {
        // Given: A new city to insert
        val city = City(id = 1, name = "London")

        // When: The city is inserted and all cities are retrieved
        dao.insertCity(city)
        val cities = dao.getAllCities().first() // Collect the first emission from the Flow

        // Then: Verify the retrieved data matches the inserted data
        assertEquals(1, cities.size) // Ensure only one city exists
        assertEquals("London", cities[0].name) // Ensure the city's name matches
    }

    /**
     * Tests that inserting a duplicate city does not cause conflicts and duplicates are ignored.
     */
    @Test
    fun insertDuplicateCity_ignoresConflict() = runBlocking {
        // Given: A city to insert
        val city = City(id = 1, name = "London")

        // When: The same city is inserted twice
        dao.insertCity(city)
        dao.insertCity(city) // Insert the duplicate
        val cities = dao.getAllCities().first()

        // Then: Verify that only one instance of the city exists
        assertEquals(1, cities.size) // Ensure no duplicates were inserted
    }

    /**
     * Tests that the database is initially empty when no cities are inserted.
     */
    @Test
    fun getAllCities_returnsEmptyListInitially() = runBlocking {
        // When: Retrieving all cities without inserting any
        val cities = dao.getAllCities().first()

        // Then: Verify the city list is empty
        assertEquals(0, cities.size) // Ensure no cities exist initially
    }
}
