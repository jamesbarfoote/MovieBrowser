package com.appydinos.moviebrowser.ui.moviedetails.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.data.model.convertedTrailer
import com.appydinos.moviebrowser.data.model.freeGuyMovie
import com.appydinos.moviebrowser.data.repo.IWatchlistRepository
import com.appydinos.moviebrowser.data.repo.MoviesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MovieDetailsViewModelUnitTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var repo: MoviesRepository
    @Mock
    lateinit var watchlistRepository: IWatchlistRepository

    private lateinit var viewModel: MovieDetailsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        viewModel = MovieDetailsViewModel(repo, watchlistRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    }

    @Test
    fun `getMovieDetails - posts movie details`() = runTest {
        //Given
        val movie = Movie(
            id = 1,
            title = "Movie title",
            description = "Some description text here",
            posterURL = "themoviedb.org/abc123",
            releaseDate = "2021-11-03",
            rating = 7.1,
            genre = listOf("Adventure", "Drama"),
            runTime = "157",
            status = "Released",
            tagLine = "In the beginning...",
            votes = 1388
        )
        `when`(repo.getMovieDetails(123)).thenReturn(movie)

        //When
        viewModel.getMovieDetails(123)

        //Then
        advanceUntilIdle()
        assertEquals(movie, viewModel.movie.value)
    }

    @Test
    fun `getMovieDetails - message view gone when details returned`() = runTest {
        //Given
        val movie = Movie(
            id = 1,
            title = "Movie title",
            description = "Some description text here",
            posterURL = "themoviedb.org/abc123",
            releaseDate = "2021-11-03",
            rating = 7.1,
            genre = listOf("Adventure", "Drama"),
            runTime = "157",
            status = "Released",
            tagLine = "In the beginning...",
            votes = 1388
        )
        `when`(repo.getMovieDetails(123)).thenReturn(movie)

        //When
        viewModel.getMovieDetails(123)

        //Then
        advanceUntilIdle()
        assertEquals(false, viewModel.messageState.value.showMessageView)
    }

    @Test
    fun `getMovieDetails - posts null when null return from server`() = runTest {
        //Given
        `when`(repo.getMovieDetails(123)).thenReturn(null)

        //When
        viewModel.getMovieDetails(123)

        //Then
        advanceUntilIdle()
        assertEquals(null, viewModel.movie.value)
    }

    @Test
    fun `getMovieDetails - posts error message when null return from server`() = runTest {
        //Given
        `when`(repo.getMovieDetails(123)).thenReturn(null)

        //When
        viewModel.getMovieDetails(123)

        //Then
        advanceUntilIdle()
        val messageState = viewModel.messageState.value
        assertEquals("Failed to get movie details", messageState.messageText)
        assertEquals(false, viewModel.showDetailsLoader.value)
        assertEquals(true, messageState.showMessageView)
        assertEquals(R.raw.details_error, messageState.messageAnimation)
        assertEquals(true, messageState.canRetry)
    }

    @Test
    fun `getMovieDetails - shows message view when movie id is less than 0`() = runTest {
        //When
        viewModel.getMovieDetails(-1)

        //Then
        advanceUntilIdle()
        val messageState = viewModel.messageState.value
        assertEquals("Select a movie to see its details", viewModel.messageState.value.messageText)
        assertEquals(false, viewModel.showDetailsLoader.value)
        assertEquals(true, messageState.showMessageView)
        assertEquals(R.raw.loader_movie, messageState.messageAnimation)
        assertEquals(false, messageState.canRetry)
    }

    @Test
    fun `getMovieDetails - posts error message when exception thrown`() = runTest {
        //Given
        given(repo.getMovieDetails(123)).willAnswer {
            throw java.lang.Exception(
                "Something happened"
            )
        }

        //When
        viewModel.getMovieDetails(123)

        //Then
        advanceUntilIdle()
        val messageState = viewModel.messageState.value
        assertEquals("Something went wrong when trying to get the movie details", viewModel.messageState.value.messageText)
        assertEquals(false, viewModel.showDetailsLoader.value)
        assertEquals(true, messageState.showMessageView)
        assertEquals(R.raw.details_error, messageState.messageAnimation)
        assertEquals(true, messageState.canRetry)
    }

    @Test
    fun `addToWatchList() call add movie on repo`() = runTest {
        //Given
        `when`(repo.getMovieDetails(123)).thenReturn(freeGuyMovie)
        viewModel.getMovieDetails(123)
        advanceUntilIdle()

        //When
        viewModel.addToWatchList()

        //Then
        advanceUntilIdle()
        verify(watchlistRepository).addMovie(freeGuyMovie)
        assertEquals(true, viewModel.isInWatchlist.value)
    }

    @Test
    fun `setMovie() updates stateflow`() = runTest {
        //Given
        assertEquals(null, viewModel.movie.value)

        //When
        viewModel.setMovie(freeGuyMovie)

        //Then
        advanceUntilIdle()
        assertEquals(freeGuyMovie, viewModel.movie.value)
        assertEquals(false, viewModel.showDetailsLoader.value)
    }

    @Test
    fun `setMovie() updated isInWatchlist after checking if movie is in watchlist db`() = runTest {
        //Given
        `when`(watchlistRepository.getMovieDetails(2)).thenReturn(freeGuyMovie)

        //When
        viewModel.setMovie(freeGuyMovie)

        //Then
        advanceUntilIdle()
        assertEquals(true, viewModel.isInWatchlist.value)
    }

    @Test
    fun `setMovie() updated isInWatchlist after checking if movie is in watchlist db is false when not present`() = runTest {
        //Given
        `when`(watchlistRepository.getMovieDetails(2)).thenReturn(null)

        //When
        viewModel.setMovie(freeGuyMovie)

        //Then
        assertEquals(false, viewModel.isInWatchlist.value)
    }

    @Test
    fun `setMovie - gets videos when none are present`() = runTest {
        // Given
        `when`(watchlistRepository.getMovieDetails(2)).thenReturn(freeGuyMovie)
        val videos = listOf(convertedTrailer, convertedTrailer.copy(
            id = "abc123",
            name = "The other trailer",
            key = "thisKey",
            url = "https://www.youtube.com/watch?v=thisKey",
            thumbnail = "https://img.youtube.com/vi/thisKey/0.jpg"
        ))
        `when`(repo.getMovieVideos(2)).thenReturn(videos)

        // When
        viewModel.setMovie(freeGuyMovie)

        // Then
        verify(repo).getMovieVideos(freeGuyMovie.id)
        verify(watchlistRepository).updateTrailers(freeGuyMovie.copy(videos = videos))
        assertEquals(videos, viewModel.movie.value!!.videos)
        assertEquals(false, viewModel.showDetailsLoader.value)
    }

    @Test
    fun `removeFromWatchlist - calls delete on the repo for the current movie`() = runTest {
        //Given
        `when`(repo.getMovieDetails(123)).thenReturn(freeGuyMovie.copy(id = 123))
        viewModel.getMovieDetails(123)
        advanceUntilIdle()

        //When
        viewModel.removeFromWatchlist()

        //Then
        advanceUntilIdle()
        verify(watchlistRepository).deleteMovie(123)
        assertEquals(false, viewModel.isInWatchlist.value)
    }

    @Test
    fun `updateMovie - adds videos to movie when there are videos`() = runTest {
        // Given
        val movie = Movie(
            id = 1,
            title = "Movie title",
            description = "Some description text here",
            posterURL = "themoviedb.org/abc123",
            releaseDate = "2021-11-03",
            rating = 7.1,
            genre = listOf("Adventure", "Drama"),
            runTime = "157",
            status = "Released",
            tagLine = "In the beginning...",
            votes = 1388
        )
        `when`(repo.getMovieDetails(123)).thenReturn(movie)
        val videos = listOf(convertedTrailer, convertedTrailer.copy(
            id = "abc123",
            name = "The other trailer",
            key = "thisKey",
            url = "https://www.youtube.com/watch?v=thisKey",
            thumbnail = "https://img.youtube.com/vi/thisKey/0.jpg"
        ))
        `when`(repo.getMovieVideos(123)).thenReturn(videos)

        // When
        viewModel.getMovieDetails(123)

        // Then
        advanceUntilIdle()
        assertEquals(movie.copy(videos = videos), viewModel.movie.value)
    }

    @Test
    fun `updateMovie - doesn't update movie when no videos`() = runTest {
        // Given
        val movie = Movie(
            id = 1,
            title = "Movie title",
            description = "Some description text here",
            posterURL = "themoviedb.org/abc123",
            releaseDate = "2021-11-03",
            rating = 7.1,
            genre = listOf("Adventure", "Drama"),
            runTime = "157",
            status = "Released",
            tagLine = "In the beginning...",
            votes = 1388
        )
        `when`(repo.getMovieDetails(123)).thenReturn(movie)
        `when`(repo.getMovieVideos(123)).thenReturn(null)

        // When
        viewModel.getMovieDetails(123)

        // Then
        advanceUntilIdle()
        assertEquals(movie.copy(videos = null), viewModel.movie.value)
    }
}
