package com.example.simpleweatherapp.presentation

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.ui.theme.SimpleWeatherAppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun viewModelInjectionWorks() {
        val viewModel = getPrivateViewModel(composeTestRule.activity)
        assert(viewModel != null)
    }

    @Test
    fun appLaunchesSuccessfully() {
        composeTestRule.setContent {
            SimpleWeatherAppTheme {
                WeatherApp(viewModel = getPrivateViewModel(composeTestRule.activity)!!)
            }
        }

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.app_name))
            .assertIsDisplayed()
    }

    /**
     * Retrieves the private `viewModel` field from `MainActivity` using reflection.
     */
    private fun getPrivateViewModel(activity: MainActivity): WeatherViewModel? {
        return MainActivity::class.declaredMemberProperties
            .find { it.name == "viewModel" } // Locate the field named "viewModel"
            ?.apply { isAccessible = true } // Make it accessible
            ?.getter
            ?.call(activity) as? WeatherViewModel
    }
}
