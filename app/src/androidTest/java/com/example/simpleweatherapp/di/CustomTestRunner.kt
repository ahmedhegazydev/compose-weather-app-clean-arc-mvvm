package com.example.simpleweatherapp.di

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * CustomTestRunner class for configuring the test environment in the application.
 *
 * This class is used to replace the default AndroidJUnitRunner with a custom runner that
 * uses the HiltTestApplication for dependency injection during testing.
 *
 * @see AndroidJUnitRunner
 * @see HiltTestApplication
 */
class CustomTestRunner : AndroidJUnitRunner() {

    /**
     * Overrides the default application class for the test environment.
     *
     * This method replaces the application class with the HiltTestApplication to ensure
     * that Hilt can properly manage dependency injection during tests.
     *
     * @param cl The class loader to use for creating the application instance.
     * @param className The name of the application class. This is replaced with
     *                  HiltTestApplication for testing purposes.
     * @param context The base context for the application.
     * @return The application instance configured for the test environment.
     */
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
