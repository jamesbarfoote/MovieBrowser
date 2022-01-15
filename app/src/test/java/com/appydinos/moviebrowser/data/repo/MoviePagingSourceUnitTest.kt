package com.appydinos.moviebrowser.data.repo

import androidx.paging.PagingSource
import com.appydinos.moviebrowser.data.model.MovieListResponse
import com.appydinos.moviebrowser.data.model.freeGuyMovieList
import com.appydinos.moviebrowser.data.model.movieListResponse
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
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MoviePagingSourceUnitTest {

    @Mock
    lateinit var movieService: MovieService
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    }

    @Test
    fun `load - returns page when successful`() = runTest {
        //Given
        `when`(movieService.getLatestMovies(1)).thenReturn(Response.success(movieListResponsePage1))

        //When
        val pageSource = MoviePagingSource(movieService, null)

        //Then
        assertEquals(
            PagingSource.LoadResult.Page(
                data = listOf(freeGuyMovieList, freeGuyMovieList.copy(title = "Eternals", id = 3)),
                prevKey = null,
                nextKey = 2
            ),
            pageSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `load - returns next page key null when reached the end`() = runTest {
        //Given
        `when`(movieService.getLatestMovies(2)).thenReturn(
            Response.success(
                movieListResponsePage1.copy(
                    page = 2
                )
            )
        )

        //When
        val pageSource = MoviePagingSource(movieService, null)

        //Then
        assertEquals(
            PagingSource.LoadResult.Page(
                data = listOf(freeGuyMovieList, freeGuyMovieList.copy(title = "Eternals", id = 3)),
                prevKey = null,
                nextKey = null
            ),
            pageSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 2,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `load - failure response sets error message`() = runTest {
        //Given
        `when`(movieService.getLatestMovies(2)).thenReturn(
            Response.error(
                404,
                "Failed to get movies"
                    .toResponseBody("application/json".toMediaTypeOrNull())
            )
        )

        //When
        val pageSource = MoviePagingSource(movieService, null)

        //Then
        val loadResult = pageSource.load(
            PagingSource.LoadParams.Refresh(
                key = 2,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )
        assertEquals(
            true,
            loadResult is PagingSource.LoadResult.Error
        )
        assertEquals(
            "Error(throwable=java.lang.Exception: Failed to get movies)",
            (loadResult as PagingSource.LoadResult.Error).toString()
        )
    }

    @Test
    fun `load - IOException sets error message`() = runTest {
        //Given
        given(movieService.getLatestMovies(2)).willAnswer {
            throw IOException(
                "Failed to communicate with the server"
            )
        }

        //When
        val pageSource = MoviePagingSource(movieService, null)

        //Then
        val loadResult = pageSource.load(
            PagingSource.LoadParams.Refresh(
                key = 2,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )
        assertEquals(
            "java.io.IOException: Failed to communicate with the server",
            (loadResult as PagingSource.LoadResult.Error).throwable.toString()
        )
    }

    @Test
    fun `load - HttpException sets error message`() = runTest {
        //Given
        given(movieService.getLatestMovies(2)).willAnswer {
            throw HttpException(
                Response.error<Any>(400, "Failed to reach the internet"
                    .toResponseBody("plain/text".toMediaTypeOrNull())
                )
            )
        }

        //When
        val pageSource = MoviePagingSource(movieService, null)

        //Then
        val loadResult = pageSource.load(
            PagingSource.LoadParams.Refresh(
                key = 2,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )
        assertEquals(
            true,
            (loadResult as PagingSource.LoadResult.Error).throwable is HttpException
        )
    }
}

val movieListResponsePage1 = MovieListResponse(
    dates = MovieListResponse.Dates(
        maximum = "",
        minimum = ""
    ), page = 1, results = listOf(movieListResponse, movieListResponse.copy(title = "Eternals", id = 3)), totalPages = 2, totalResults = 4
)
