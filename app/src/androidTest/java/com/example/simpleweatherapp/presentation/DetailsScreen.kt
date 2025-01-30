package com.example.simpleweatherapp.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.simpleweatherapp.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displaysCityNameInAppBar() {
        val cityName = "New York"
        val timestamp = "12:00 PM"

        composeTestRule.setContent {
            DetailsScreen(
                description = "Sunny",
                temperature = "30°C",
                humidity = "50%",
                windSpeed = "10 km/h",
                iconUrl = "https://example.com/icon.png",
                cityName = cityName,
                timestamp = timestamp,
                onDismiss = {}
            )
        }

        composeTestRule
            .onNodeWithText("Weather information for $cityName received on $timestamp")
            .assertIsDisplayed()
    }

    @Test
    fun callsOnDismissWhenBackButtonClicked() {
        var onDismissCalled = false
        val onDismissLambda: () -> Unit = { onDismissCalled = true }

        composeTestRule.setContent {
            DetailsScreen(
                description = "Sunny",
                temperature = "30°C",
                humidity = "50%",
                windSpeed = "10 km/h",
                iconUrl = "https://example.com/icon.png",
                cityName = "Cairo",
                timestamp = "10:00 AM",
                        onDismiss = onDismissLambda

            )
        }

        composeTestRule
            .onNodeWithContentDescription("Back", useUnmergedTree = true)
            .performClick()

        assert(onDismissCalled) { "onDismiss was not called when the back button was clicked" }
    }

    @Test
    fun displaysWeatherDetailsCorrectly() {
        val description = "Sunny"
        val temperature = "30°C"
        val humidity = "50%"
        val windSpeed = "10 km/h"

        composeTestRule.setContent {
            DetailsScreen(
                description = description,
                temperature = temperature,
                humidity = humidity,
                windSpeed = windSpeed,
                iconUrl = "https://example.com/icon.png",
                cityName = "Cairo",
                timestamp = "10:00 AM",
                onDismiss = {}
            )
        }

        // Get string resources using the activity context
        val expectedDescription = composeTestRule.activity.getString(R.string.description, description)
        val expectedTemp = composeTestRule.activity.getString(R.string.temp_c, temperature)
        val expectedHumidity = composeTestRule.activity.getString(R.string.humidity, humidity)
        val expectedWind = composeTestRule.activity.getString(R.string.wind_km_h, windSpeed)

        composeTestRule.onNodeWithText(expectedDescription).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedTemp).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedHumidity).assertIsDisplayed()
        composeTestRule.onNodeWithText(expectedWind).assertIsDisplayed()
    }

    @Test
    fun displaysWeatherIcon() {
        composeTestRule.setContent {
            DetailsScreen(
                description = "Sunny",
                temperature = "30°C",
                humidity = "50%",
                windSpeed = "10 km/h",
                iconUrl = "https://example.com/icon.png",
                cityName = "Cairo",
                timestamp = "10:00 AM",
                onDismiss = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Weather Icon")
            .assertIsDisplayed()
    }

    @Test
    fun displaysTimestampCorrectly() {
        val timestamp = "10:00 AM"
        val cityName = "Cairo"

        composeTestRule.setContent {
            DetailsScreen(
                description = "Sunny",
                temperature = "30°C",
                humidity = "50%",
                windSpeed = "10 km/h",
                iconUrl = "https://example.com/icon.png",
                cityName = "Cairo",
                timestamp = timestamp,
                onDismiss = {}
            )
        }

        composeTestRule
            .onNodeWithText("Weather information for $cityName received on $timestamp")
            .assertIsDisplayed()
    }
}
