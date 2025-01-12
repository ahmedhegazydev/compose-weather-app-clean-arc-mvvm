package com.example.simpleweatherapp.presentation

import android.net.Uri
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.simpleweatherapp.data.NavigationType
import com.example.simpleweatherapp.domain.WeatherViewState
import com.google.gson.Gson

/**
 * A wrapper for data that is exposed via a LiveData that represents an event.
 *
 * @param T The type of content being wrapped.
 *
 * This class prevents the content from being handled multiple times by keeping
 * track of whether it has been accessed or not.
 */
class SingleEvent<out T>(private val content: T) {

    // Flag to check if the event has been handled.
    private var hasBeenHandled = false;

    /**
     * Returns the content if it hasn't been handled yet, otherwise returns null.
     *
     * Once the content is accessed through this method, it is marked as handled.
     *
     * @return The content if not handled, otherwise null.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null;
        } else {
            hasBeenHandled = true;
            content;
        }
    }

    /**
     * Returns the content even if it has already been handled.
     *
     * This can be used to inspect the value without marking it as handled.
     *
     * @return The content.
     */
    fun peekContent(): T = content;
}


/**
 * Handles navigation to different screens based on the provided NavigationType.
 *
 * @param navigationType The type of navigation (e.g., DETAILS or HISTORICAL).
 * @param success The WeatherViewState object containing weather details to be passed as arguments.
 * @param navController The NavHostController that manages navigation within the app.
 */
fun handleNavigation(
    navigationType: NavigationType,
    success: WeatherViewState,
    navController: NavHostController
) {
    val gson = Gson()
    when (navigationType) {
        NavigationType.DETAILS -> {
            // Constructs the route for the details screen using encoded weather details.
            val route = "details/${encodeWeatherDetails(success)}"
            navController.navigate(route)
        }
        NavigationType.HISTORICAL -> {
            // Converts the WeatherViewState to a JSON string and encodes it for navigation.
            val weathersJson = gson.toJson(success)
            val encodedWeathersJson = Uri.encode(weathersJson)
            navController.navigate("historical/$encodedWeathersJson/${Uri.encode(success.cityName)}")
        }
        else -> {
            // No action for unsupported navigation types.
        }
    }
}

/**
 * Encodes the details of a WeatherViewState object into a URL-safe string for navigation.
 *
 * @param weather The WeatherViewState object containing weather details.
 * @return A slash-separated string of URL-encoded weather details.
 *
 * Example output:
 * "Sunny/25.0/60/15.0/icon_url/City%20Name/2025-01-01"
 */
fun encodeWeatherDetails(weather: WeatherViewState): String {
    return listOf(
        weather.description,
        weather.temperature,
        weather.humidity,
        weather.windSpeed,
        weather.iconUrl,
        weather.cityName,
        weather.timestamp
    ).joinToString("/") { Uri.encode(it) }
}

/**
 * Creates a list of NavArgument objects for navigation composables.
 *
 * @param args A variable number of argument names required by the route.
 * @return A list of NavArgument objects with type String.
 *
 * Example usage:
 * createNavArguments("description", "temperature", "humidity")
 * // Returns a list of NavArgument objects for "description", "temperature", and "humidity".
 */
fun createNavArguments(vararg args: String) = args.map { navArgument(it) { type = NavType.StringType } }

/**
 * Extracts weather details from a NavBackStackEntry and returns them as a map.
 *
 * @param backStackEntry The NavBackStackEntry containing route arguments.
 * @return A map where the key is the argument name, and the value is the corresponding value from the back stack entry.
 *
 * Example output:
 * {"description" to "Sunny", "temperature" to "25.0", "humidity" to "60"}
 */
fun getWeatherDetails(backStackEntry: NavBackStackEntry): Map<String, String> {
    return backStackEntry.arguments?.let { args ->
        args.keySet().associateWith { args.getString(it) ?: "" }
    } ?: emptyMap()
}
