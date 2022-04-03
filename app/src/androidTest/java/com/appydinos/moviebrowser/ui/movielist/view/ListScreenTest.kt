package com.appydinos.moviebrowser.ui.movielist.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    val movie = Movie(
        id = 2,
        title = "Free Guy",
        description = "A bank teller called Guy realizes...",
        posterURL = "https://image.tmdb.org/t/p/w500/freeguy.img",
        releaseDate = "2021-08-11",
        rating = 7.2,
        genre = listOf("Comedy", "Adventure"),
        runTime = "1h 55m",
        status = "Released",
        tagLine = "Life's too short to be a background character.",
        votes = 4038
    )

    @Test
    fun movie_list_item_correct_content_shown() {
        //Given
        var hasMovieBeenClicked = false

        //When
        composeTestRule.setContent {
            MovieBrowserTheme(windows = null) {
                MovieListItem(movie = movie) { hasMovieBeenClicked = !hasMovieBeenClicked }
            }
        }

        //Then
        composeTestRule.onNodeWithContentDescription("Free Guy")
            .assertIsDisplayed()
            .assertHeightIsAtLeast(300.dp)
            .assertWidthIsAtLeast(100.dp)
            .performClick()
        assertEquals(true, hasMovieBeenClicked)

        composeTestRule.onNodeWithTag("RatingIcon", useUnmergedTree = true)
            .assertIsDisplayed()
            .onChildAt(0).assertTextEquals("7.2")
        composeTestRule.onNodeWithText("Free Guy")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("A bank teller called Guy realizes...")
            .assertIsDisplayed()
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun movie_list_shows_multiple_movies() {
        //Given
        var selectedMovieId = -1
        val movieItems = listOf(
            movie,
            movie.copy(title = "Red Notice", rating = 7.5, id = 1234),
            movie.copy(title = "The Adam Project", rating = 7.0, id = 3)
        )
        val movieList = flowOf(PagingData.from(movieItems))
        val lazyListState = LazyListState(0, 0)


        //When
        composeTestRule.setContent {
            MovieBrowserTheme(windows = null) {
                ListScreen(
                    state = lazyListState,
                    flow = movieList
                ) {
                    selectedMovieId = it
                }
            }
        }

        //Then
        composeTestRule.onNodeWithContentDescription("Free Guy")
            .assertIsDisplayed().performTouchInput { swipeUp() }
        composeTestRule.onNodeWithContentDescription("Red Notice")
            .assertIsDisplayed().performClick().performTouchInput { swipeUp() }
        assertEquals(1234, selectedMovieId)
        composeTestRule.onNodeWithContentDescription("The Adam Project")
            .assertIsDisplayed()
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun movie_list_shows_loading_animation_when_no_movies() {
        //Given
        val movieItems = listOf<Movie>()
        val movieList = flowOf(PagingData.from(movieItems))
        val lazyListState = LazyListState(0, 0)


        //When
        composeTestRule.setContent {
            MovieBrowserTheme(windows = null) {
                ListScreen(
                    state = lazyListState,
                    flow = movieList
                ) { }
            }
        }

        //Then
        composeTestRule.onNodeWithTag("Footer Animation")
            .assertIsDisplayed()
    }

}