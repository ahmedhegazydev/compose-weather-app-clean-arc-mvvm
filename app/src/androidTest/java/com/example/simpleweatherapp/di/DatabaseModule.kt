package com.example.simpleweatherapp.di

import com.example.simpleweatherapp.data.offline.AppDatabase
import com.example.simpleweatherapp.data.offline.CityDao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.simpleweatherapp.MyApp
import com.example.simpleweatherapp.data.offline.City
import dagger.hilt.android.testing.CustomTestApplication
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DatabaseModuleTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var cityDao: CityDao

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testDatabaseIsProvidedCorrectly() {
        assertNotNull(db)
        assertNotNull(cityDao)
    }

    @Test
    fun testCityDaoCanInsertAndRetrieveData() = runBlocking {
        // Given
        val city = City(name = "Paris")

        // When
        cityDao.insertCity(listOf(city))

        val cities = cityDao.getAllCities().first()

        // Then
        assertEquals(1, cities.size)
        assertEquals("Paris", cities[0].name)
    }

    @Test
    fun testCityDaoReturnsEmptyInitially() = runBlocking {
        // Clear database
        db.clearAllTables()

        // When
        val cities = cityDao.getAllCities().first()

        // Then
        assertEquals(0, cities.size) // Assert that the database is empty
    }


    @Test
    fun testCityDaoIgnoresDuplicateInsert() = runBlocking {
        // Given
        val city = City(name = "London")

        // When
        cityDao.insertCity(listOf(city, city))

        val cities = cityDao.getAllCities().first()

        // Then
        assertEquals(1, cities.size) // Duplicate should not be added
    }
}


