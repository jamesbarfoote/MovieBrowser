package com.appydinos.moviebrowser.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.data.model.Video

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

    @Query("UPDATE Watchlist SET videos = :trailers WHERE movie_id=:movieId")
    fun updateMovieTrailers(movieId: Int, trailers: List<Video>)
}
