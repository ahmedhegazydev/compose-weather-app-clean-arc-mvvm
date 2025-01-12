package com.example.simpleweatherapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.simpleweatherapp.presentation.CitiesTopAppBar
import org.junit.Rule
import org.junit.Test

class CitiesTopAppBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysTitleCorrectly() {
        val titleText = "Test Title"

        composeTestRule.setContent {
            CitiesTopAppBar(
                title = titleText
            )
        }

        composeTestRule
            .onNodeWithText(titleText)
            .assertIsDisplayed()
    }

    @Test
    fun displaysNavigationIconWhenProvided() {
        composeTestRule.setContent {
            CitiesTopAppBar(
                title = "Test Title",
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Back")
            .assertIsDisplayed()
    }

    @Test
    fun displaysRightIconWhenProvided() {
        composeTestRule.setContent {
            CitiesTopAppBar(
                title = "Test Title",
                rightIcon = { modifier ->
                    Box(modifier) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu"
                        )
                    }
                }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Menu")
            .assertIsDisplayed()
    }

    @Test
    fun doesNotCrashWithoutNavigationIcon() {
        composeTestRule.setContent {
            CitiesTopAppBar(
                title = "Test Title"
            )
        }

        composeTestRule
            .onNodeWithText("Test Title")
            .assertExists()
    }

    @Test
    fun doesNotCrashWithoutRightIcon() {
        composeTestRule.setContent {
            CitiesTopAppBar(
                title = "Test Title"
            )
        }

        composeTestRule
            .onNodeWithText("Test Title")
            .assertExists()
    }

//    @Test
//    fun alignsTitleCorrectly() {
//        composeTestRule.setContent {
//            CitiesTopAppBar(
//                title = "Centered Title"
//            )
//        }
//
//        composeTestRule
//            .onNodeWithText("Centered Title")
//            .assert(hasParent(hasTestTag("TopAppBarTitle")))
//    }
}
