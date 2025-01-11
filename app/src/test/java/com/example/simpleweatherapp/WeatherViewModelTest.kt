package com.example.simpleweatherapp

import app.cash.turbine.test
import com.example.simpleweatherapp.data.NavigationType
import com.example.simpleweatherapp.data.offline.City
import com.example.simpleweatherapp.domain.AddCityUseCase
import com.example.simpleweatherapp.domain.GetCitiesUseCase
import com.example.simpleweatherapp.domain.GetWeatherUseCase
import com.example.simpleweatherapp.domain.Weather
import com.example.simpleweatherapp.domain.WeatherViewState
import com.example.simpleweatherapp.domain.toViewState
import com.example.simpleweatherapp.presentation.WeatherViewModel
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private lateinit var viewModel: WeatherViewModel
    private val getWeatherUseCase: GetWeatherUseCase = mockk()
    private val getCitiesUseCase: GetCitiesUseCase = mockk()
    private val addCityUseCase: AddCityUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = WeatherViewModel(getWeatherUseCase, getCitiesUseCase, addCityUseCase)
    }

    @Test
    fun `setNavigationType updates navigation type`() = runTest {
        viewModel.setNavigationType(NavigationType.DETAILS)
        viewModel.navigationType.test {
            assertEquals(NavigationType.DETAILS, awaitItem())
        }
    }

    @Test
    fun `loadCities loads default cities when data source is empty`() = runTest {
        // Mock `getCitiesUseCase.execute` to return an empty list
        coEvery { getCitiesUseCase.execute() } returns flowOf(emptyList())

        // Mock `addCityUseCase.execute` to just run
        coEvery { addCityUseCase.execute(any()) } just Runs

        // Instantiate the ViewModel
        viewModel = WeatherViewModel(getWeatherUseCase, getCitiesUseCase, addCityUseCase)

        // Verify that `addCityUseCase.execute` is called 3 times
        coVerify(exactly = 3) { addCityUseCase.execute(any()) }
    }


    @Test
    fun `addCity adds a new city`() = runTest {
        val cityName = "Cairo"
        coEvery { addCityUseCase.execute(any()) } just Runs

        viewModel.addCity(cityName)

        advanceUntilIdle()
        coVerify { addCityUseCase.execute(City(name = cityName)) }
    }

    @Test
    fun `fetchWeather updates weather state on success`() = runTest {
        val city = City(name = "Paris")
        val weather = mockk<Weather>()
        val weatherViewState = WeatherViewState(
            description = "Clear",
            temperature = "20°C",
            humidity = "50%",
            windSpeed = "5 km/h",
            iconUrl = "http://example.com/icon.png",
            cityName = "Paris",
            timestamp = "10.01.2025 - 12:00",
            location = "Lat: 48.8566, Lon: 2.3522",
            weathers = emptyList()
        )

        coEvery { getWeatherUseCase.invoke(city.name) } returns weather
        every { weather.toViewState() } returns weatherViewState

        viewModel.fetchWeather(city)
        viewModel.weatherState.test {
            advanceUntilIdle()
            assertEquals(WeatherViewModel.WeatherState.Success(weatherViewState), awaitItem())
        }
    }

    @Test
    fun `fetchWeather updates weather state on error`() = runTest {
        val city = City(name = "InvalidCity")
        val errorMessage = "City not found"
        coEvery { getWeatherUseCase.invoke(city.name) } throws RuntimeException(errorMessage)

        viewModel.fetchWeather(city)
        viewModel.weatherState.test {
            advanceUntilIdle()
            assertEquals(
                WeatherViewModel.WeatherState.Error(errorMessage),
                awaitItem()
            )
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
