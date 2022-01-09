package com.appydinos.moviebrowser.ui.moviedetails.viewmodel

import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.data.repo.MoviesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MovieDetailsViewModelUnitTest {

    @Mock
    lateinit var repo: MoviesRepository

    private lateinit var viewModel: MovieDetailsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        viewModel = MovieDetailsViewModel(repo)
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
        assertEquals(false, viewModel.showMessageView.value)
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
        assertEquals("Failed to get movie details", viewModel.messageText.value)
        assertEquals(false, viewModel.showDetailsLoader.value)
        assertEquals(true, viewModel.showMessageView.value)
        assertEquals(R.raw.details_error, viewModel.messageAnimation.value)
        assertEquals(true, viewModel.canRetry.value)
    }

    @Test
    fun `getMovieDetails - shows message view when movie id is less than 0`() = runTest {
        //When
        viewModel.getMovieDetails(-1)

        //Then
        advanceUntilIdle()
        assertEquals("Select a movie to see its details", viewModel.messageText.value)
        assertEquals(false, viewModel.showDetailsLoader.value)
        assertEquals(true, viewModel.showMessageView.value)
        assertEquals(R.raw.loader_movie, viewModel.messageAnimation.value)
        assertEquals(false, viewModel.canRetry.value)
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
        assertEquals("Something went wrong when trying to get the movie details", viewModel.messageText.value)
        assertEquals(false, viewModel.showDetailsLoader.value)
        assertEquals(true, viewModel.showMessageView.value)
        assertEquals(R.raw.details_error, viewModel.messageAnimation.value)
        assertEquals(true, viewModel.canRetry.value)
    }
}