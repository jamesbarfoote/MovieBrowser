package com.appydinos.moviebrowser.data.repo

import com.appydinos.moviebrowser.data.db.AppDatabase
import com.appydinos.moviebrowser.data.db.WatchlistDao
import com.appydinos.moviebrowser.data.db.WatchlistItem
import com.appydinos.moviebrowser.data.model.freeGuyMovie
import com.appydinos.moviebrowser.testextensions.capture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class WatchlistRepositoryUnitTest {

    @Mock
    private lateinit var database: AppDatabase
    @Mock
    private lateinit var watchlistDao: WatchlistDao

    private lateinit var repo: WatchlistRepository

    private val testDispatcher = StandardTestDispatcher()

    @Captor
    private lateinit var captor: ArgumentCaptor<WatchlistItem>


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        repo = WatchlistRepository(database)
        `when`(database.watchlistDao()).thenReturn(watchlistDao)
    }

    @After
    fun tearDown() {
        database.close()
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    }

    @Test
    fun `addMovie - adds to database`() = runTest {
        //Given
        val movie = freeGuyMovie

        //When
        repo.addMovie(movie)

        //Then
        verify(watchlistDao).addToWatchlist(capture(captor))
        assertEquals(movie, captor.value.movie)
        assertEquals(Date().toString(), captor.value.addedAt.toString())
    }

    @Test
    fun deleteMovie() = runTest {
        //When
        repo.deleteMovie(1)

        //Then
        verify(watchlistDao).deleteMovie(1)
    }

    @Test
    fun getMovieDetails() = runTest {
        //Given
        `when`(watchlistDao.getMovie(1)).thenReturn(freeGuyMovie)

        //When
        val result = repo.getMovieDetails(1)

        //Then
        assertEquals(freeGuyMovie, result)
    }
}
