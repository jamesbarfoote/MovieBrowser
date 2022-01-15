package com.appydinos.moviebrowser.ui.watchlist.viewmodel

import androidx.paging.PagingData
import androidx.paging.map
import com.appydinos.moviebrowser.data.db.WatchlistItem
import com.appydinos.moviebrowser.data.model.freeGuyMovie
import com.appydinos.moviebrowser.data.model.testMovie
import com.appydinos.moviebrowser.data.repo.IWatchlistRepository
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class WatchlistViewModelUnitTest {

    @Mock
    lateinit var repo: IWatchlistRepository
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: WatchlistViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        viewModel = WatchlistViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `deleteMovie - calls delete on repo`() = runTest {
        //When
        viewModel.deleteMovie(1)

        //Then
        advanceUntilIdle()
        verify(repo).deleteMovie(1)
    }

    @Test
    fun `getWatchList - makes call to get watchlist`() = runTest {
        //Given
        val watchlistItems = listOf(
            WatchlistItem(id = 1, movie = freeGuyMovie, addedAt = Date()),
            WatchlistItem(id = 2, movie = testMovie, addedAt = Date())
        )
        `when`(repo.getWatchlist()).thenReturn(flowOf(PagingData.from(watchlistItems)))

        //When
        viewModel.watchList.first()

        //Then
        advanceUntilIdle()
        verify(repo).getWatchlist()
    }

    @Test
    fun `getWatchList - result is correct`() = runTest {
        //Given
        val watchlistItems = listOf(
            WatchlistItem(id = 1, movie = freeGuyMovie, addedAt = Date()),
            WatchlistItem(id = 2, movie = testMovie, addedAt = Date())
        )
        `when`(repo.getWatchlist()).thenReturn(flowOf(PagingData.from(watchlistItems)))

        //When
        val result = viewModel.watchList.first()

        //Then
        advanceUntilIdle()
        result.map {
            assertEquals(watchlistItems, it)
        }
    }
}
