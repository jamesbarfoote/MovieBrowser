package com.appydinos.moviebrowser.data.repo

import androidx.paging.PagingData
import com.appydinos.moviebrowser.data.dp.WatchlistItem
import com.appydinos.moviebrowser.data.model.Movie
import kotlinx.coroutines.flow.Flow

interface IWatchlistRepository {
    suspend fun getWatchlist(): Flow<PagingData<WatchlistItem>>

    suspend fun addMovie(movie: Movie): Long?

    suspend fun deleteMovie(movieId: Int)

    suspend fun getMovieDetails(movieId: Int): Movie?
}