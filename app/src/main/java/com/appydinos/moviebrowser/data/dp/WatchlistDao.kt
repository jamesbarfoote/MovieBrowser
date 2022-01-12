package com.appydinos.moviebrowser.data.dp

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.appydinos.moviebrowser.data.model.Movie

@Dao
interface WatchlistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToWatchlist(movie: Movie): Long?

    @Query("SELECT * FROM Watchlist ORDER BY watchListedAt desc")
    fun fetchAllWatchlistMovies(): PagingSource<Int, Movie>


    @Query("SELECT * FROM Watchlist WHERE id =:movieId")
    fun getMovie(movieId: Int): Movie?

    @Query("DELETE FROM Watchlist WHERE id=:movieId")
    fun deleteMovie(movieId: Int)
}