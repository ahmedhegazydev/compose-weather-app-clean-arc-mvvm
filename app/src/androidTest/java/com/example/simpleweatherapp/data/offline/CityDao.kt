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

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class CityDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: CityDao

    @Before
    fun setUp() {
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.cityDao()
    }

    @After
    fun tearDown() {
        // Close the database after tests
        database.close()
    }

    @Test
    fun insertCity_and_getAllCities() = runBlocking {
        // Given
        val city = City(id = 1, name = "London")

        // When
        dao.insertCity(city)
        val cities = dao.getAllCities().first()

        // Then
        assertEquals(1, cities.size)
        assertEquals("London", cities[0].name)
    }

    @Test
    fun insertDuplicateCity_ignoresConflict() = runBlocking {
        // Given
        val city = City(id = 1, name = "London")

        // When
        dao.insertCity(city)
        dao.insertCity(city) // Attempt to insert duplicate
        val cities = dao.getAllCities().first()

        // Then
        assertEquals(1, cities.size) // Duplicate should be ignored
    }

    @Test
    fun getAllCities_returnsEmptyListInitially() = runBlocking {
        // When
        val cities = dao.getAllCities().first()

        // Then
        assertEquals(0, cities.size) // No cities should exist initially
    }
}
