package com.appydinos.moviebrowser.data.db

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.appydinos.moviebrowser.data.model.Movie
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var watchlistDao: WatchlistDao

    private val freeGuyMovie = Movie(
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

    val testMovie = Movie(
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

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        watchlistDao = database.watchlistDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun adds_movie_to_db() {
        //Given
        val watchlistItem = WatchlistItem(movie = freeGuyMovie, addedAt = Date())

        //When
        watchlistDao.addToWatchlist(watchlistItem)

        //Then
        assertEquals(watchlistItem.movie, watchlistDao.getMovie(2))
    }

    @Test
    @Throws(Exception::class)
    fun fetch_all_movies() = runBlocking {
        //Given
        val watchlistItem = WatchlistItem(id = 1, movie = freeGuyMovie, addedAt = Date())
        val watchlistItemTwo = WatchlistItem(id = 2, movie = testMovie, addedAt = Date())

        //When
        watchlistDao.addToWatchlist(watchlistItem)
        watchlistDao.addToWatchlist(watchlistItemTwo)

        //Then
        assertEquals(
            PagingSource.LoadResult.Page(
                data = listOf(watchlistItem, watchlistItemTwo),
                prevKey = null,
                nextKey = null,
                itemsAfter = 0,
                itemsBefore = 0
            ), watchlistDao.fetchAllWatchlistMovies().load(
                PagingSource.LoadParams.Refresh(
                    key = 0,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun delete_movie_from_dp() {
        //Given
        val watchlistItem = WatchlistItem(movie = freeGuyMovie, addedAt = Date())
        val watchlistItemTwo = WatchlistItem(id = 2, movie = testMovie, addedAt = Date())
        watchlistDao.addToWatchlist(watchlistItem)
        watchlistDao.addToWatchlist(watchlistItemTwo)

        //When
        watchlistDao.deleteMovie(2)

        //Then
        assertEquals(null, watchlistDao.getMovie(2))
        assertEquals(watchlistItemTwo.movie, watchlistDao.getMovie(1))
    }

}