package com.appydinos.moviebrowser.ui.settings

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsContent_all_elements_shown() {
        //Given
        var hasLogoBeenClicked = false
        var hasOSSLicensesBeenClicked = false

        //When
        composeTestRule.setContent {
            MovieBrowserTheme(windows = null) {
                SettingsContent(
                    onLogoClicked = { hasLogoBeenClicked = !hasLogoBeenClicked },
                onOSSClicked = { hasOSSLicensesBeenClicked = !hasOSSLicensesBeenClicked})
            }
        }

        //Then
        val image = composeTestRule.onNodeWithContentDescription("TMDB Logo", useUnmergedTree = true)
        image.assertIsDisplayed().performClick()
        assertEquals(true, hasLogoBeenClicked)

        val allDataText = composeTestRule.onNodeWithTag("All data text", useUnmergedTree = true)
        allDataText.assertIsDisplayed().assertTextEquals("All movie data provided by The Movie Database (TMDB) themoviedb.org")

        val ossLicensesButton = composeTestRule.onNodeWithTag("OSS Licenses Button", useUnmergedTree = true)
        ossLicensesButton.assertIsDisplayed().performClick().onChildAt(0).assertTextEquals("Open Source Licenses")
        assertEquals(true, hasOSSLicensesBeenClicked)

    }
}