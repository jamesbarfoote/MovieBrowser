package com.appydinos.moviebrowser.ui.movielist.viewmodel

import androidx.paging.PagingData
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.data.model.freeGuyMovieList
import com.appydinos.moviebrowser.data.repo.MoviesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MovieListViewModelUnitTest {

    @Mock
    lateinit var repo: MoviesRepository

    private lateinit var viewModel: MovieListViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        `when`(repo.getNowPlayingMovies(30)).thenReturn(flowOf(PagingData.from(
            listOf(freeGuyMovieList))))
        viewModel = MovieListViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    }

    @Test
    fun `getMoviesList - makes call to get now playing movies`() = runTest {
        //When
        MutableStateFlow<List<Movie>>(listOf())
            .flatMapLatest { viewModel.pagingData }

        //Then
        advanceUntilIdle()
        verify(repo).getNowPlayingMovies(30)
    }

    @Test
    fun `getMoviesList - result is correct`() = runTest {
        //When
        val result = MutableStateFlow<List<Movie>>(listOf())
            .flatMapLatest { viewModel.pagingData }

        //Then
        advanceUntilIdle()
        result.map {
            assertEquals(freeGuyMovieList, it)
        }
    }
}
