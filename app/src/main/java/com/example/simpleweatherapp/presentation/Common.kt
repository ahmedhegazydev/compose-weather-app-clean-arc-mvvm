package com.example.simpleweatherapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A custom TopAppBar composable function for displaying a title, a navigation icon,
 * and an optional right-aligned icon in the app bar.
 *
 * @param title The title to display in the TopAppBar.
 * @param navigationIcon A composable lambda for providing the navigation icon (e.g., a back button).
 * @param rightIcon A composable lambda for providing the right-aligned icon,
 * which can be customized with a modifier for alignment.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitiesTopAppBar(
    title: String, // The title displayed in the center of the app bar.
    navigationIcon: @Composable (() -> Unit) = {}, // Optional navigation icon, defaults to an empty composable.
    rightIcon: @Composable (Modifier) -> Unit = {}, // Optional right icon composable, customizable with a modifier.
) {
    // TopAppBar is a Material 3 API component that acts as the main app bar for the screen.
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Fills the available width of the app bar.
                    .padding(16.dp) // Adds padding around the content for spacing.
            ) {
                // Title text, styled with MaterialTheme typography, centered in the app bar.
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center) // Centers the title within the Box.
                )

                // The rightIcon composable is aligned to the end of the app bar.
                rightIcon(Modifier.align(Alignment.CenterEnd))
            }
        },
        navigationIcon = navigationIcon // Sets the navigation icon in the TopAppBar.
    )
}
