package com.example.simpleweatherapp


import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.example.simpleweatherapp.data.NavigationType
import com.example.simpleweatherapp.data.offline.City
import com.example.simpleweatherapp.domain.NavigationData
import com.example.simpleweatherapp.domain.WeatherViewState
import com.example.simpleweatherapp.presentation.MainActivity
import com.example.simpleweatherapp.presentation.SingleEvent
import com.example.simpleweatherapp.presentation.WeatherApp
import com.example.simpleweatherapp.presentation.WeatherViewModel
import dagger.hilt.android.testing.CustomTestApplication
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
class WeatherAppTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)


    private lateinit var navController: TestNavHostController
    private lateinit var mockViewModel: WeatherViewModel

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()

        navController = TestNavHostController(composeTestRule.activity)
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        // Mock WeatherViewModel
        mockViewModel = mockk(relaxed = true)

        // Mock cities state
        every { mockViewModel.cities } returns MutableStateFlow(
            listOf(City(name = "Cairo"), City(name = "Alexandria"))
        )

        // Mock navigation events
        every { mockViewModel.navigationSingedEvent } returns MutableStateFlow(
            SingleEvent(WeatherViewModel.NavigationState.Idle)
        )
    }

    @Test
    fun displaysCityListScreenInitially() {
        composeTestRule.setContent {
            WeatherApp(viewModel = mockViewModel)
        }

        // Check if the CityListScreen is displayed
        composeTestRule.onNodeWithText("Cities").assertIsDisplayed()
    }

    @Test
    fun navigatesToDetailsScreenOnCityClick() {
        // Mock navigation to details screen
        every { mockViewModel.navigate(any(), NavigationType.DETAILS) } answers {
            val city = firstArg<City>()
            val viewState = WeatherViewState(
                description = "Sunny",
                temperature = "25°C",
                humidity = "60%",
                windSpeed = "15 km/h",
                iconUrl = "",
                cityName = city.name,
                timestamp = "12:00 PM",
                location = "Lat: 30.0444, Lon: 31.2357",
                weathers = emptyList()
            )
            (mockViewModel.navigationSingedEvent as MutableStateFlow).value = SingleEvent(
                WeatherViewModel.NavigationState.Success(
                    NavigationData(
                        navigationType = NavigationType.DETAILS,
                        viewState = viewState,
                        isLoading = false
                    )
                )
            )
        }

        composeTestRule.setContent {
            WeatherApp(viewModel = mockViewModel)
        }

        // Click on a city to navigate to the details screen
        composeTestRule.onNodeWithText("Cairo").performClick()

        // Check if the DetailsScreen is displayed
        composeTestRule.onNodeWithText("Weather details for Cairo").assertIsDisplayed()
    }

    @Test
    fun displaysAddCityDialogAndAddsCity() {
        // Mock adding a city
        every { mockViewModel.addCity(any()) } answers {
            val cityName = firstArg<String>()
            val currentCities = (mockViewModel.cities as MutableStateFlow).value.toMutableList()
            currentCities.add(City(name = cityName))
            (mockViewModel.cities as MutableStateFlow).value = currentCities
        }

        composeTestRule.setContent {
            WeatherApp(viewModel = mockViewModel)
        }

        // Click the "Add City" button
        composeTestRule.onNodeWithContentDescription("Add City").performClick()

        // Check if the dialog is displayed
        composeTestRule.onNodeWithText("Add City").assertIsDisplayed()

        // Enter a city name and confirm
        composeTestRule.onNodeWithText("Enter city name").performTextInput("Luxor")
        composeTestRule.onNodeWithText("Add").performClick()

        // Verify the new city is added
        composeTestRule.onNodeWithText("Luxor").assertIsDisplayed()
    }
}



