package com.example.simpleweatherapp.presentation

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.navigation.NavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.Navigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.example.simpleweatherapp.data.NavigationType
import com.example.simpleweatherapp.domain.WeatherDescription
import com.example.simpleweatherapp.domain.WeatherViewState
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class PresentationTests {

    @Test
    fun testSingleEventReturnsContentIfNotHandled() {
        val event = SingleEvent("TestContent")
        assertEquals("TestContent", event.getContentIfNotHandled())
        assertNull(event.getContentIfNotHandled())
    }

    @Test
    fun testSingleEventPeekContentAlwaysReturnsContent() {
        val event = SingleEvent("TestContent")
        assertEquals("TestContent", event.peekContent())
        event.getContentIfNotHandled()
        assertEquals("TestContent", event.peekContent())
    }

    @Test
    fun testEncodeWeatherDetailsReturnsCorrectFormat() {
        val weather = WeatherViewState(
            description = "Sunny",
            temperature = "25.0",
            humidity = "60",
            windSpeed = "15.0",
            iconUrl = "icon_url",
            cityName = "City Name",
            timestamp = "2025-01-01",
            location = "Lat: 40.7128, Lon: -74.0060",
            weathers = listOf(
                WeatherDescription(
                    id = 800,
                    main = "Clear",
                    description = "Clear sky",
                    icon = "01d"
                )
            )
        )
        val expected = "Sunny/25.0/60/15.0/icon_url/City%20Name/2025-01-01"
        assertEquals(expected, encodeWeatherDetails(weather))
    }

    @Test
    fun testHandleNavigationNavigatesToDetailsRoute() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            // Create TestNavHostController and attach lifecycle
            val navController = TestNavHostController(context).apply {
                setLifecycleOwner(TestLifecycleOwner().apply {
                    currentState = Lifecycle.State.RESUMED
                })
                navigatorProvider.addNavigator(NavGraphNavigator(navigatorProvider))
            }

            // Create a NavGraph
            val navGraph =
                NavGraph(navController.navigatorProvider.getNavigator(NavGraphNavigator::class.java)).apply {
                    id = 1 // ID for the NavGraph

                    // Define the "details" destination
                    val detailsDestination = NavDestination("details").apply {
                        id = 2 // Unique ID for the destination
                        route =
                            "details/{description}/{temperature}/{humidity}/{windSpeed}/{iconUrl}/{cityName}/{timestamp}" // Route with placeholders
                        addArgument(
                            "description",
                            NavArgument.Builder().setType(NavType.StringType).build()
                        )
                        addArgument(
                            "temperature",
                            NavArgument.Builder().setType(NavType.StringType).build()
                        )
                        addArgument(
                            "humidity",
                            NavArgument.Builder().setType(NavType.StringType).build()
                        )
                        addArgument(
                            "windSpeed",
                            NavArgument.Builder().setType(NavType.StringType).build()
                        )
                        addArgument(
                            "iconUrl",
                            NavArgument.Builder().setType(NavType.StringType).build()
                        )
                        addArgument(
                            "cityName",
                            NavArgument.Builder().setType(NavType.StringType).build()
                        )
                        addArgument(
                            "timestamp",
                            NavArgument.Builder().setType(NavType.StringType).build()
                        )
                    }

                    addDestination(detailsDestination) // Add the destination to the graph
                    setStartDestination(detailsDestination.id) // Set the starting destination
                }

            // Set the navigation graph to the NavController
            navController.graph = navGraph

            // Define a WeatherViewState object
            val weather = WeatherViewState(
                description = "Sunny",
                temperature = "25.0",
                humidity = "60",
                windSpeed = "15.0",
                iconUrl = "icon_url",
                cityName = "City Name",
                timestamp = "2025-01-01",
                location = "Lat: 40.7128, Lon: -74.0060",
                weathers = listOf(
                    WeatherDescription(
                        id = 800,
                        main = "Clear",
                        description = "Clear sky",
                        icon = "01d"
                    )
                )
            )

            // Perform navigation
            handleNavigation(NavigationType.DETAILS, weather, navController)

            // Verify arguments passed to the destination
            val arguments = navController.currentBackStackEntry?.arguments
            assertNotNull(arguments)
            assertEquals("Sunny", arguments?.getString("description"))
            assertEquals("25.0", arguments?.getString("temperature"))
            assertEquals("60", arguments?.getString("humidity"))
            assertEquals("15.0", arguments?.getString("windSpeed"))
            assertEquals("icon_url", arguments?.getString("iconUrl"))
            assertEquals("City Name", arguments?.getString("cityName"))
            assertEquals("2025-01-01", arguments?.getString("timestamp"))
        }
    }


    @Test
    fun testHandleNavigationNavigatesToHistoricalRoute() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            // Create TestNavHostController
            val navController = TestNavHostController(context).apply {
                setLifecycleOwner(TestLifecycleOwner().apply {
                    currentState = Lifecycle.State.RESUMED
                })
                navigatorProvider.addNavigator(NavGraphNavigator(navigatorProvider))
            }

            // Create a NavGraph and add the historical destination
            val navGraph =
                NavGraph(navController.navigatorProvider.getNavigator(NavGraphNavigator::class.java)).apply {
                    id = 1 // ID for the NavGraph

                    // Use NavDestination with correct type
                    val historicalDestination = NavDestination("historical").apply {
                        id = 2 // Unique ID for the destination
                        route =
                            "historical/{weatherJson}/{cityName}" // Define route with placeholders
                        addArgument(
                            "weatherJson",
                            NavArgument.Builder().setType(NavType.StringType).build()
                        )
                        addArgument(
                            "cityName",
                            NavArgument.Builder().setType(NavType.StringType).build()
                        )
                    }

                    addDestination(historicalDestination) // Add the destination to the graph
                    setStartDestination(historicalDestination.id) // Set as start destination
                }

            // Set the navigation graph
            navController.graph = navGraph

            // Define a WeatherViewState object
            val weather = WeatherViewState(
                description = "Sunny",
                temperature = "25.0",
                humidity = "60",
                windSpeed = "15.0",
                iconUrl = "icon_url",
                cityName = "City Name",
                timestamp = "2025-01-01",
                location = "Lat: 40.7128, Lon: -74.0060",
                weathers = listOf(
                    WeatherDescription(
                        id = 800,
                        main = "Clear",
                        description = "Clear sky",
                        icon = "01d"
                    )
                )
            )

            // Perform navigation
            handleNavigation(NavigationType.HISTORICAL, weather, navController)

            // Construct the expected arguments
            val gson = Gson()
            val expectedJson = gson.toJson(weather)
            val expectedCityName = weather.cityName

            // Retrieve arguments from the current back stack entry
            val arguments = navController.currentBackStackEntry?.arguments
            val actualWeatherJson = arguments?.getString("weatherJson")
            val actualCityName = arguments?.getString("cityName")

            // Verify arguments
            assertEquals(expectedJson, actualWeatherJson)
            assertEquals(expectedCityName, actualCityName)
        }
    }

    @Test
    fun testCreateNavArgumentsCreatesCorrectArguments() {
        val args = createNavArguments("arg1", "arg2", "arg3")
        assertEquals(3, args.size)
//        assertTrue(args.all { it.argumentType == NavType.StringType })
        assertEquals("arg1", args[0].name)
        assertEquals("arg2", args[1].name)
        assertEquals("arg3", args[2].name)
    }

    @Test
    fun testGetWeatherDetailsExtractsArgumentsCorrectly() {
        val context = ApplicationProvider.getApplicationContext<Context>()


        // Create a real Bundle with the test arguments
        val arguments = Bundle().apply {
            putString("key1", "value1")
            putString("key2", "value2")
        }

        // Create a real NavBackStackEntry
        val navBackStackEntry = NavBackStackEntry.create(
            context,
            arguments = arguments,
            destination = NavDestination("test_destination"),
            savedState = null
        )

        // Call the function to extract arguments
        val result = getWeatherDetails(navBackStackEntry)

        // Expected map of arguments
        val expected = mapOf("key1" to "value1", "key2" to "value2")

        // Assert that the result matches the expected map
        assertEquals(expected, result)
    }
}

