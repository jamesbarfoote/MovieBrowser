package com.appydinos.moviebrowser.ui.moviedetails.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.data.model.Video
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import com.appydinos.moviebrowser.ui.moviedetails.model.MessageState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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
    fun details_toolbar_shows_correctly() {
        //Given
        var hasBackBeenPressed = false
        var hasRemoveFromWatchlistBeenPressed = false
        var hasAddToWatchlistBeenPressed = false

        val messageState = MessageState(
            showMessageView = false,
            messageText = "Some message text",
            messageAnimation = R.raw.details_error,
            animationAspectRatio = 1.0f,
            canRetry = false
        )

        //When
        val isInWatchlist = MutableStateFlow(true)
        composeTestRule.setContent {
            MovieBrowserTheme(windows = null) {
                DetailsScreen(
                    inWatchlist = isInWatchlist,
                    twoPane = MutableStateFlow(false),
                    currentMovie = MutableStateFlow(freeGuyMovie),
                    shouldShowLoader = MutableStateFlow(false),
                    messageState = MutableStateFlow(messageState),
                    isFromWatchlist = false,
                    onBackPressed = { hasBackBeenPressed = !hasBackBeenPressed },
                    onTrailerClicked = {},
                    removeFromWatchlist = {
                        isInWatchlist.value = !isInWatchlist.value
                        hasRemoveFromWatchlistBeenPressed = !hasRemoveFromWatchlistBeenPressed
                                          },
                    addToWatchlist = {
                        isInWatchlist.value = !isInWatchlist.value
                        hasAddToWatchlistBeenPressed = !hasAddToWatchlistBeenPressed
                                     },
                    onLoadRetry = {}
                )
            }
        }

        //Then
        //Verify toolbar content
        val toolbar = composeTestRule.onNodeWithTag("DetailsToolbar", useUnmergedTree = true)
            .assertIsDisplayed()
        toolbar.onChildAt(0)
            .onChildAt(0)
            .assertContentDescriptionEquals("Back")
            .assertIsDisplayed()
            .performClick()
        toolbar.onChildAt(1)
            .assertTextEquals("Details")
            .assertIsDisplayed()
        toolbar.onChildAt(2)
            .onChildAt(0)
            .assertContentDescriptionEquals("Remove from watchlist")
            .assertIsDisplayed()
            .performClick()
        assertEquals(true, hasBackBeenPressed)
        assertEquals(true, hasRemoveFromWatchlistBeenPressed)
        toolbar.onChildAt(2)
            .onChildAt(0)
            .assertContentDescriptionEquals("Add to watchlist")
            .performClick()
        assertEquals(true, hasAddToWatchlistBeenPressed)
    }

    @Test
    fun details_toolbar_shows_correctly_when_two_pane() {
        //Given
        var hasBackBeenPressed = false
        var hasRemoveFromWatchlistBeenPressed = false
        var hasAddToWatchlistBeenPressed = false

        val messageState = MessageState(
            showMessageView = false,
            messageText = "Some message text",
            messageAnimation = R.raw.details_error,
            animationAspectRatio = 1.0f,
            canRetry = false
        )

        //When
        val isInWatchlist = MutableStateFlow(false)
        composeTestRule.setContent {
            MovieBrowserTheme(windows = null) {
                DetailsScreen(
                    inWatchlist = isInWatchlist,
                    twoPane = MutableStateFlow(true),
                    currentMovie = MutableStateFlow(freeGuyMovie),
                    shouldShowLoader = MutableStateFlow(false),
                    messageState = MutableStateFlow(messageState),
                    isFromWatchlist = false,
                    onBackPressed = { hasBackBeenPressed = !hasBackBeenPressed },
                    onTrailerClicked = {},
                    removeFromWatchlist = {
                        isInWatchlist.value = !isInWatchlist.value
                        hasRemoveFromWatchlistBeenPressed = !hasRemoveFromWatchlistBeenPressed
                                          },
                    addToWatchlist = {
                        isInWatchlist.value = !isInWatchlist.value
                        hasAddToWatchlistBeenPressed = !hasAddToWatchlistBeenPressed
                                     },
                    onLoadRetry = {}
                )
            }
        }

        //Then
        val toolbar = composeTestRule.onNodeWithTag("DetailsToolbar", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back")
            .assertDoesNotExist()
        toolbar.onChildAt(1)
            .onChildAt(0)
            .assertContentDescriptionEquals("Add to watchlist")
            .assertIsDisplayed()
            .performClick()
    }

    @Test
    fun details_message_content_shows_correctly() {
        //Given
        var hasClickedRetry = false
        var messageState = MessageState(
            showMessageView = true,
            messageText = "Some message text",
            messageAnimation = R.raw.details_error,
            animationAspectRatio = 1.0f,
            canRetry = true
        )

        //When
        val shouldShowLoader = MutableStateFlow(false)
        composeTestRule.setContent {
            MovieBrowserTheme(windows = null) {
                DetailsScreen(
                    inWatchlist = MutableStateFlow(true),
                    twoPane = MutableStateFlow(false),
                    currentMovie = MutableStateFlow(freeGuyMovie),
                    shouldShowLoader = shouldShowLoader,
                    messageState = MutableStateFlow(messageState),
                    isFromWatchlist = false,
                    onBackPressed = {},
                    onTrailerClicked = {},
                    removeFromWatchlist = {},
                    addToWatchlist = {},
                    onLoadRetry = {
                        messageState = messageState.copy(showMessageView = false)
                        hasClickedRetry = !hasClickedRetry
                    }
                )
            }
        }

        //Then
        val messageView = composeTestRule.onNodeWithTag("DetailsMessageView", useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("MessageViewAnimation")
            .assertIsDisplayed()

        messageView.onChildAt(1)
            .assertIsDisplayed()
            .assertTextEquals("Some message text")
        messageView.onChildAt(2)
            .assertIsDisplayed()
            .onChildAt(0)
            .assertTextEquals("Retry")

        messageView.onChildAt(2).performClick()
        assertEquals(true, hasClickedRetry)
    }

    @Test
    fun details_content_shows_correctly() {
        //Given
        val messageState = MessageState(
            showMessageView = false,
            messageText = "Some message text",
            messageAnimation = R.raw.details_error,
            animationAspectRatio = 1.0f,
            canRetry = false
        )

        //When
        val shouldShowLoader = MutableStateFlow(false)
        composeTestRule.setContent {
            MovieBrowserTheme(windows = null) {
                DetailsScreen(
                    inWatchlist = MutableStateFlow(true),
                    twoPane = MutableStateFlow(false),
                    currentMovie = MutableStateFlow(freeGuyMovie),
                    shouldShowLoader = shouldShowLoader,
                    messageState = MutableStateFlow(messageState),
                    isFromWatchlist = false,
                    onBackPressed = {},
                    onTrailerClicked = {},
                    removeFromWatchlist = {},
                    addToWatchlist = {},
                    onLoadRetry = {}
                )
            }
        }

        //Then
        composeTestRule.onNodeWithContentDescription("Free Guy")
            .assertIsDisplayed()
            .assertHeightIsAtLeast(350.dp)
            .assertWidthIsAtLeast(100.dp)
        composeTestRule.onNodeWithText("Free Guy")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("1h 55m | 2021-08-11 | Comedy, Adventure")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Life's too short to be a background character.")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Rating")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("7.8 (4038 votes)")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Overview")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("A bank teller called Guy realizes...")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Trailers")
            .assertIsDisplayed()
    }

    @Test
    fun details_trailers_show_correctly() {
        //Given
        var selectedTrailerUrl = ""
        val messageState = MessageState(
            showMessageView = false,
            messageText = "Some message text",
            messageAnimation = R.raw.details_error,
            animationAspectRatio = 1.0f,
            canRetry = false
        )
        val trailer = Video(id = "1", key = "abc123", name = "Trailer1", site = "youtube", type = "video", url = "www.youtube.com/t1", thumbnail = "https://img.youtube.com/vi/LFbhGEiFWk4/0.jpg")
        val currentMovie = freeGuyMovie.copy(
            videos = listOf(
                trailer,
                trailer.copy(id = "2", key = "def456", name = "Trailer2", url = "www.youtube.com/t2"),
                trailer.copy(id = "3", key = "789", name = "Trailer3", url = "www.youtube.com/t3")
            )
        )

        //When
        composeTestRule.setContent {
            MovieBrowserTheme(windows = null) {
                DetailsScreen(
                    inWatchlist = MutableStateFlow(true),
                    twoPane = MutableStateFlow(false),
                    currentMovie = MutableStateFlow(currentMovie),
                    shouldShowLoader = MutableStateFlow(false),
                    messageState = MutableStateFlow(messageState),
                    isFromWatchlist = false,
                    onBackPressed = {},
                    onTrailerClicked = { url -> selectedTrailerUrl = url },
                    removeFromWatchlist = {},
                    addToWatchlist = {},
                    onLoadRetry = {}
                )
            }
        }

        //Then

        val trailers = composeTestRule.onNodeWithTag("DetailsTrailers", useUnmergedTree = true)
            .assertIsDisplayed()
        trailers.onChildren()[0].assertContentDescriptionEquals("Free Guy. Trailer1")
        trailers.onChildren()[1]
            .assertContentDescriptionEquals("Free Guy. Trailer2")
            .performTouchInput { swipeLeft() }
            .performClick()

        trailers.onChildren()[2]
            .assertContentDescriptionEquals("Free Guy. Trailer3")
            .performTouchInput { swipeLeft() }

        assertEquals("www.youtube.com/t2", selectedTrailerUrl)
    }


}