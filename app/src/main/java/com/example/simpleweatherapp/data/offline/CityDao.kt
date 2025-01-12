package com.example.simpleweatherapp.data.offline

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
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


    /**
     * Retrieves a city by its name.
     *
     * @param name The name of the city to retrieve.
     * @return The [City] object matching the name, or null if not found.
     */
    @Query("SELECT * FROM cities WHERE name = :name LIMIT 1")
    suspend fun getCityByName(name: String): City?

    /**
     * Deletes all cities from the database.
     */
    @Query("DELETE FROM cities")
    suspend fun deleteAllCities()


    /**
     * Updates an existing city in the database.
     *
     * @param city The [City] object with updated information.
     * @return The number of rows updated (should be 1 if successful).
     */
    @Update
    suspend fun updateCity(city: City): Int


    /**
     * Deletes a specific city from the database.
     *
     * @param city The [City] object to delete.
     */
    @Delete
    suspend fun deleteCity(city: City)


}
