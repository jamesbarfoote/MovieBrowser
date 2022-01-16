package com.appydinos.moviebrowser.data.repo

import com.appydinos.moviebrowser.data.model.MovieResponse
import com.appydinos.moviebrowser.data.model.freeGuyMovie
import com.appydinos.moviebrowser.data.model.movieResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MoviesRepositoryUnitTest {

    @Mock
    lateinit var api: MovieService
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repo: MoviesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        repo = MoviesRepository(api)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    }

    @Test
    fun `getMovieDetails - returns movie`() = runTest {
        //Given
        `when`(api.getMovie(123)).thenReturn(Response.success(movieResponse))

        //When
        val result = repo.getMovieDetails(123)

        //Then
        assertEquals(freeGuyMovie, result)
    }

    @Test
    fun `getMovieDetails - returns null when not a success`() = runTest {
        //Given
        `when`(api.getMovie(123)).thenReturn(Response.error<MovieResponse>(500, "Failed to reach the internet"
            .toResponseBody("plain/text".toMediaTypeOrNull())
        ))

        //When
        val result = repo.getMovieDetails(123)

        //Then
        assertEquals(null, result)
    }
}
