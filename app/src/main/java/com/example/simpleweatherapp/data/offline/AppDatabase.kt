package com.example.simpleweatherapp.data.offline

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The main database class for the application.
 * This class serves as the database holder and is the main access point
 * to the app's persisted data.
 *
 * @Database annotation is used to define the entities and the version of the database.
 *
 * @property entities A list of all entities (tables) in the database.
 * @property version The version of the database schema.
 * @property exportSchema Whether to export the database schema for version control (false here for simplicity).
 */
@Database(entities = [City::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to the DAO (Data Access Object) for the `City` table.
     *
     * @return An instance of the `CityDao` interface for database operations.
     */
    abstract fun cityDao(): CityDao
}
