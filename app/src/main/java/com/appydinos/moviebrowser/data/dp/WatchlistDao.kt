package com.appydinos.moviebrowser.data.dp

import androidx.paging.PagingSource
import androidx.room.*
import com.appydinos.moviebrowser.data.model.Movie

@Dao
interface WatchlistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToWatchlist(movie: WatchlistItem): Long?

    @Query("SELECT * FROM Watchlist ORDER BY added_at desc")
    fun fetchAllWatchlistMovies(): PagingSource<Int, WatchlistItem>

    @Query("SELECT * FROM Watchlist WHERE movie_id=:movieId")
    fun getMovie(movieId: Int): Movie?

    @Query("DELETE FROM Watchlist WHERE movie_id=:movieId")
    fun deleteMovie(movieId: Int)
}