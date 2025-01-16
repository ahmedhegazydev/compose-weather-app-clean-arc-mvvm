package com.example.simpleweatherapp.presentation

import android.content.Context
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.simpleweatherapp.domain.WeatherViewState
import org.junit.Rule
import org.junit.Test
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.domain.WeatherDescription

class HistoricalWeatherScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysCityNameInTopAppBar() {
        val cityName = "Cairo"
        val weatherViewState = WeatherViewState(
            description = "",
            temperature = "",
            humidity = "",
            windSpeed = "",
            iconUrl = "",
            cityName = cityName,
            timestamp = "",
            location = "",
            weathers = emptyList()
        )

        composeTestRule.setContent {
            HistoricalWeatherScreen(
                weather = weatherViewState,
                cityName = cityName,
                onDismiss = {}
            )
        }

        // Resolve the string resource with the city name
        val context = ApplicationProvider.getApplicationContext<Context>()
        val expectedTitle = context.getString(R.string.historical, cityName)

        // Assert that the expected title is displayed
        composeTestRule.onNodeWithText(expectedTitle)
            .assertIsDisplayed()
    }

    @Test
    fun callsOnDismissWhenDoneButtonClicked() {
        var onDismissCalled = false
        val weatherViewState = WeatherViewState(
            description = "",
            temperature = "",
            humidity = "",
            windSpeed = "",
            iconUrl = "",
            cityName = "Cairo",
            timestamp = "",
            location = "",
            weathers = emptyList()
        )

        composeTestRule.setContent {
            HistoricalWeatherScreen(
                weather = weatherViewState,
                cityName = "Cairo",
                onDismiss = { onDismissCalled = true }
            )
        }

        composeTestRule
            .onNodeWithText("Done")
            .performClick()

        assert(onDismissCalled)
    }

//    @Test
//    fun displaysWeatherItemsInLazyColumn() {
//        val weatherDescriptions = listOf(
//            WeatherDescription(id = 1, main = "Clear", description = "Sunny", icon = "01d"),
//            WeatherDescription(id = 2, main = "Clouds", description = "Cloudy", icon = "02d")
//        )
//        val weatherViewState = WeatherViewState(
//            description = "",
//            temperature = "",
//            humidity = "",
//            windSpeed = "",
//            iconUrl = "",
//            cityName = "Cairo",
//            timestamp = "",
//            location = "",
//            weathers = weatherDescriptions
//        )
//
//        composeTestRule.setContent {
//            HistoricalWeatherScreen(
//                weather = weatherViewState,
//                cityName = "Cairo",
//                onDismiss = {}
//            )
//        }
//
//        composeTestRule
//            .onNodeWithText("Sunny")
//            .assertIsDisplayed()
//
//        composeTestRule
//            .onNodeWithText("Cloudy")
//            .assertIsDisplayed()
//    }

    @Test
    fun displaysArrowForwardIconInWeatherItem() {
        composeTestRule.setContent {
            HistoricalWeatherItem(
                dateTime = "2025-01-01 10:00",
                temperature = "25°C",
                weatherDescription = "Sunny"
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Details")
            .assertIsDisplayed()
    }

    @Test
    fun displaysWeatherDetailsInWeatherItem() {
        val dateTime = "2025-01-01 10:00"
        val temperature = "25°C"
        val description = "Sunny"

        composeTestRule.setContent {
            HistoricalWeatherItem(
                dateTime = dateTime,
                temperature = temperature,
                weatherDescription = description
            )
        }

        composeTestRule
            .onNodeWithText(dateTime)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("$description, $temperature")
            .assertIsDisplayed()
    }
}
