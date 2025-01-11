package com.example.simpleweatherapp.data.offline

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the `City` table.
 *
 * This interface provides methods to interact with the database,
 * allowing operations such as querying and inserting data.
 */
@Dao
interface CityDao {

    /**
     * Retrieves all cities stored in the `cities` table.
     *
     * This function returns a [Flow] of a list of cities, enabling reactive
     * data updates. Whenever the data in the database changes, any observer
     * subscribed to this [Flow] will receive the updated data.
     *
     * @return A [Flow] emitting a list of all [City] entries in the database.
     */
    @Query("SELECT * FROM cities")
    fun getAllCities(): Flow<List<City>>

    /**
     * Inserts a new city into the `cities` table.
     *
     * If a city with the same primary key already exists, the conflict strategy
     * [OnConflictStrategy.IGNORE] ensures that the duplicate insertion is ignored.
     *
     * @param city The [City] object to be inserted into the database.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(city: City)
}
