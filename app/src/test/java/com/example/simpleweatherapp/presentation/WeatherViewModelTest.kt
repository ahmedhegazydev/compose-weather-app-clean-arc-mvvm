package com.example.simpleweatherapp.presentation

import app.cash.turbine.test
import com.example.simpleweatherapp.data.NavigationType
import com.example.simpleweatherapp.data.offline.City
import com.example.simpleweatherapp.domain.*
import com.example.simpleweatherapp.domain.AddCityUseCase
import com.example.simpleweatherapp.domain.GetCitiesUseCase
import com.example.simpleweatherapp.domain.GetWeatherUseCase
import com.example.simpleweatherapp.domain.WeatherViewState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class WeatherViewModelTest {

    private lateinit var getWeatherUseCase: GetWeatherUseCase
    private lateinit var getCitiesUseCase: GetCitiesUseCase
    private lateinit var addCityUseCase: AddCityUseCase
    private lateinit var viewModel: WeatherViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        getWeatherUseCase = mock(GetWeatherUseCase::class.java) // Properly mocked
        getCitiesUseCase = mock(GetCitiesUseCase::class.java)
        addCityUseCase = mock(AddCityUseCase::class.java)
        viewModel = WeatherViewModel(getWeatherUseCase, getCitiesUseCase, addCityUseCase)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `navigate emits Loading and Success states`() = runTest {
        val city = City(name = "London")
        val navigationType = NavigationType.DETAILS
        val mockWeather = WeatherViewState(
            cityName = "London",
            temperature = "20.0",
            description = "Clear sky",
            humidity = "50",
            windSpeed = "5.0",
            iconUrl = "http://example.com/icon.png",
            timestamp = System.currentTimeMillis().toString(),
            location = "Lat: 51.5074, Lon: -0.1278",
            weathers = listOf(
                WeatherDescription(
                    id = 800,
                    main = "Clear",
                    description = "Clear sky",
                    icon = "01d"
                ),
                WeatherDescription(
                    id = 801,
                    main = "Clouds",
                    description = "Few clouds",
                    icon = "02d"
                )
            )
        )


        `when`(getWeatherUseCase(city.name)).thenReturn(mockWeather.toWeather())

        viewModel.navigationSingedEvent.test {
            viewModel.navigate(city, navigationType)

            assertEquals(
                WeatherViewModel.NavigationState.Loading,
                awaitItem()?.getContentIfNotHandled()
            )
            val successState =
                awaitItem()?.getContentIfNotHandled() as WeatherViewModel.NavigationState.Success
            assertEquals("London", successState.data.viewState?.cityName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `navigate emits Error state on exception`() = runTest {
        val city = City(name = "InvalidCity")
        val navigationType = NavigationType.DETAILS

        `when`(getWeatherUseCase(city.name)).thenThrow(RuntimeException("City not found"))

        viewModel.navigationSingedEvent.test {
            viewModel.navigate(city, navigationType)

            assertEquals(
                WeatherViewModel.NavigationState.Loading,
                awaitItem()?.getContentIfNotHandled()
            )
            val errorState =
                awaitItem()?.getContentIfNotHandled() as WeatherViewModel.NavigationState.Error
            assertEquals("City not found", errorState.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadCities adds default cities if none exist`() = runTest {
        `when`(getCitiesUseCase.execute()).thenReturn(flowOf(emptyList()))

        viewModel.cities.test {

            // Use reflection to access the private loadCities method
            val loadCitiesMethod = WeatherViewModel::class.java.getDeclaredMethod("loadCities")
            loadCitiesMethod.isAccessible = true // Make the method accessible

            // Invoke the private method
            loadCitiesMethod.invoke(viewModel)

            val emittedCities = awaitItem()
            assertTrue(emittedCities.isEmpty())

            verify(addCityUseCase).execute(
                listOf(
                    City(name = "London"),
                    City(name = "Paris"),
                    City(name = "Vienna"),
                )
            )

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadCities populates cities from data source`() = runTest {
        val mockCities = listOf(City(name = "Berlin"), City(name = "Tokyo"))
        `when`(getCitiesUseCase.execute()).thenReturn(flowOf(mockCities))

        viewModel.cities.test {

            // Use reflection to access the private loadCities method
            val loadCitiesMethod = WeatherViewModel::class.java.getDeclaredMethod("loadCities")
            loadCitiesMethod.isAccessible = true // Make the method accessible

            // Invoke the private method
            loadCitiesMethod.invoke(viewModel)

            assertEquals(mockCities, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addCity adds a city to the data source`() = runTest {
        val cityName = "New York"

        viewModel.addCity(cityName)

        verify(addCityUseCase).execute(listOf(City(name = cityName)))
    }
}

fun WeatherViewState.toWeather(): Weather {
    return Weather(
        coord = Coord(
            lon = location.substringAfter("Lon: ").toDoubleOrNull() ?: 0.0,
            lat = location.substringAfter("Lat: ").substringBefore(", Lon:").toDoubleOrNull() ?: 0.0
        ),
        weatherDescriptions = weathers,
        base = "stations", // Default or placeholder value
        main = Main(
            temp = temperature.toDoubleOrNull()?.plus(273.15) ?: 273.15, // Convert °C to Kelvin
            feelsLike = temperature.toDoubleOrNull()?.plus(273.15) ?: 273.15, // Approximation
            tempMin = temperature.toDoubleOrNull()?.plus(273.15) ?: 273.15, // Approximation
            tempMax = temperature.toDoubleOrNull()?.plus(273.15) ?: 273.15, // Approximation
            pressure = 1013, // Default or placeholder value
            humidity = humidity.toIntOrNull() ?: 50 // Default or derived value
        ),
        visibility = 10000, // Default or placeholder value
        wind = Wind(
            speed = windSpeed.toDoubleOrNull() ?: 0.0,
            deg = 0 // Default or placeholder value
        ),
        clouds = Clouds(all = 0), // Default or placeholder value
        timestamp = System.currentTimeMillis() / 1000, // Convert milliseconds to seconds
        systemInfo = SystemInfo(
            type = 0,
            id = 0,
            country = "", // Default or placeholder value
            sunrise = 0, // Default or placeholder value
            sunset = 0 // Default or placeholder value
        ),
        timezone = 0, // Default or placeholder value
        id = 0, // Default or placeholder value
        name = cityName,
        statusCode = 200 // Default HTTP status code
    )
}

