package com.example.simpleweatherapp.presentation;

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitiesTopAppBar(
    title: String,
    navigationIcon: @Composable (() -> Unit) = {},
    rightIcon: @Composable (Modifier) -> Unit = {},
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center)
                )

                rightIcon(Modifier.align(Alignment.CenterEnd))

            }
        },
        navigationIcon = navigationIcon
    )
}