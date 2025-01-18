package com.example.simpleweatherapp.data.offline

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test class for verifying the behavior of the `City` entity and its interactions
 * with the Room database.
 */
@RunWith(AndroidJUnit4::class)
//@SmallTest
class CityEntityTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: CityDao

    @Before
    fun setUp() {
        // Initialize an in-memory Room database
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
//            .fallbackToDestructiveMigrationOnDowngrade()
//            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

        dao = database.cityDao()
    }

    @After
    fun tearDown() {
        // Close the database after each test
        database.close()
    }

    @Test
    fun insertCity_and_verifyData() = runBlocking {
        // Given
        val city = City(name = "New York")

        // When
        dao.insertCity(listOf(city))
        val cities = dao.getAllCities().first()

        // Then
        assertEquals(1, cities.size)
        assertEquals("New York", cities[0].name)
    }

    @Test
    fun insertMultipleCities_and_verifyOrder() = runBlocking {
        // Given
        val city1 = City(name = "Paris")
        val city2 = City(name = "Berlin")

        // When
        dao.insertCity(listOf(city1, city2))

        val cities = dao.getAllCities().first()

        // Then
        assertEquals(2, cities.size)
        assertEquals("Paris", cities[0].name)
        assertEquals("Berlin", cities[1].name)
    }

    @Test
    fun insertCity_withAutoGeneratedId() = runBlocking {
        // Given
        val city = City(name = "Tokyo")

        // When
        dao.insertCity(listOf(city))

        val cities = dao.getAllCities().first()

        // Then
        assertEquals(1, cities.size)
        assertEquals(1, cities[0].id) // Auto-generated ID starts at 1
        assertEquals("Tokyo", cities[0].name)
    }

//    @Test
//    fun insertDuplicateCity_ignoresConflict() = runBlocking {
//        // Given
//        val city = City(name = "Sydney")
//
//        // When
//        dao.insertCity(city)
//        dao.insertCity(city) // Insert the same city again
//        val cities = dao.getAllCities().first()
//
//        // Then
//        assertEquals(1, cities.size) // The duplicate insertion should be ignored
//    }

    @Test
    fun getAllCities_returnsEmptyListInitially() = runBlocking {
        // When
        val cities = dao.getAllCities().first()

        // Then
        assertEquals(0, cities.size) // No cities should exist initially
    }
}
