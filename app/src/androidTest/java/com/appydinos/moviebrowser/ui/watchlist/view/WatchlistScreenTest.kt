package com.appydinos.moviebrowser.ui.watchlist.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyGridState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.appydinos.moviebrowser.data.db.WatchlistItem
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.*

class WatchlistScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val redNoticeMovie = Movie(
        id = 1,
        title = "Red Notice",
        description = "An interpol issued Red Notice is...",
        posterURL = "themoviedb.org/abc123",
        releaseDate = "2021-11-04",
        rating = 6.8,
        genre = listOf("Action", "Comedy"),
        runTime = "1h 37m",
        status = "Released",
        tagLine = "Pro and cons",
        votes = 1388
    )

    private val freeGuyMovie = Movie(
        id = 2,
        title ="Free Guy",
        description =  "A bank teller called Guy realizes...",
        posterURL = "https://image.tmdb.org/t/p/w500/freeguy.img",
        releaseDate = "2021-08-11",
        rating = 7.8,
        genre = listOf("Comedy", "Adventure"),
        runTime = "1h 55m",
        status = "Released",
        tagLine =  "Life's too short to be a background character.",
        votes = 4038
    )

    @Test
    fun watchlist_poster_shown_with_rating() {
        //Given
        var hasPosterBeenClicked = false
        var hasPosterBeenLongClicked = false

        val movie = Movie(
            id = 2,
            title = "Free Guy",
            description = "A bank teller called Guy realizes...",
            posterURL = "https://image.tmdb.org/t/p/w500/freeguy.img",
            releaseDate = "2021-08-11",
            rating = 7.8,
            genre = listOf("Comedy", "Adventure"),
            runTime = "1h 55m",
            status = "Released",
            tagLine = "Life's too short to be a background character.",
            votes = 4038
        )

        //When
        composeTestRule.setContent {
            MovieBrowserTheme(windows = null) {
                PosterWithRating(
                    movie = movie,
                    modifier = Modifier,
                    onClick = { hasPosterBeenClicked = !hasPosterBeenClicked },
                    onLongClick = { hasPosterBeenLongClicked = !hasPosterBeenLongClicked }
                )
            }
        }

        //Then
        val poster = composeTestRule.onNodeWithContentDescription("Free Guy")
        poster
            .assertIsDisplayed()
            .assertHeightIsAtLeast(300.dp)
            .assertWidthIsAtLeast(100.dp)
            .performClick()
        assertEquals(true, hasPosterBeenClicked)
        poster.performTouchInput {
            longClick()
        }
        assertEquals(true, hasPosterBeenLongClicked)

        val rating = composeTestRule.onNodeWithTag("RatingIcon")
        rating
            .assertIsDisplayed()
            .onChildAt(0).assertTextEquals("7.8")
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun watchlist_shows_multiple_movies() {
        //Given
        val watchlistItems = listOf(
            WatchlistItem(id = 1, movie = freeGuyMovie, addedAt = Date()),
            WatchlistItem(id = 2, movie = redNoticeMovie, addedAt = Date()),
            WatchlistItem(id = 3, movie = redNoticeMovie.copy(title = "The Adam Project", rating = 7.0), addedAt = Date())
        )
        val watchlist = flowOf(PagingData.from(watchlistItems))
        val lazyGridState = LazyGridState(0, 0)

        //When
        composeTestRule.setContent {
            MovieBrowserTheme(windows = null) {
                WatchlistScreen(
                    watchList = watchlist,
                    lazyGridState = lazyGridState,
                    onDeleteMovie = { },
                    onMovieSelected = { }
                )
            }
        }

        //Then
        composeTestRule.onNodeWithContentDescription("Free Guy").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Red Notice").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("The Adam Project").assertIsDisplayed()
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun watchlist_shows_bottomSheet_on_longPress() {
        //Given
        val watchlistItems = listOf(
            WatchlistItem(id = 1, movie = freeGuyMovie, addedAt = Date()),
            WatchlistItem(id = 2, movie = redNoticeMovie, addedAt = Date()),
            WatchlistItem(id = 3, movie = redNoticeMovie.copy(title = "The Adam Project", rating = 7.0), addedAt = Date())
        )
        val watchlist = flowOf(PagingData.from(watchlistItems))
        val lazyGridState = LazyGridState(0, 0)

        var hasMovieBeenDeleted = false

        //When
        composeTestRule.setContent {
            MovieBrowserTheme(windows = null) {
                WatchlistScreen(
                    watchList = watchlist,
                    lazyGridState = lazyGridState,
                    onDeleteMovie = { hasMovieBeenDeleted = !hasMovieBeenDeleted },
                    onMovieSelected = { }
                )
            }
        }

        //Then
        composeTestRule.onNodeWithContentDescription("Red Notice")
            .assertIsDisplayed()
            .performTouchInput { longClick() }
        composeTestRule.onNodeWithTag("Bottom sheet title")
            .assertIsDisplayed()
            .assertTextEquals("Red Notice")
        composeTestRule.onNodeWithText("Delete")
            .assertIsDisplayed()
            .performClick()
        assertEquals(true, hasMovieBeenDeleted)

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("Bottom sheet title")
            .assertIsNotDisplayed()
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun watchlist_shows_empty_state_when_no_movies() {
        //Given
        val watchlist = flowOf(PagingData.from(listOf<WatchlistItem>()))
        val lazyGridState = LazyGridState(0, 0)

        //When
        composeTestRule.setContent {
            MovieBrowserTheme(windows = null) {
                WatchlistScreen(
                    watchList = watchlist,
                    lazyGridState = lazyGridState,
                    onDeleteMovie = { },
                    onMovieSelected = { }
                )
            }
        }

        //Then
        composeTestRule.onNodeWithText("No movies in your Watchlist yet… Start adding and they will appear here")
            .assertIsDisplayed()
    }
}