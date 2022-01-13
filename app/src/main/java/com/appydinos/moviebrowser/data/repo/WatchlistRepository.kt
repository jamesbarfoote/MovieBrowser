package com.appydinos.moviebrowser.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.appydinos.moviebrowser.data.dp.AppDatabase
import com.appydinos.moviebrowser.data.dp.WatchlistItem
import com.appydinos.moviebrowser.data.model.Movie
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class WatchlistRepository @Inject constructor(private val appDatabase: AppDatabase): IWatchlistRepository {

    override suspend fun getWatchlist(): Flow<PagingData<WatchlistItem>> {
        return Pager(PagingConfig(20)) { appDatabase.watchlistDao().fetchAllWatchlistMovies() }.flow
    }

    override suspend fun addMovie(movie: Movie): Long? {
        return appDatabase.watchlistDao().addToWatchlist(WatchlistItem(movie = movie, addedAt = Date()))
    }

    override suspend fun deleteMovie(movieId: Int) {
        appDatabase.watchlistDao().deleteMovie(movieId)
    }

    override suspend fun getMovieDetails(movieId: Int): Movie? {
        return appDatabase.watchlistDao().getMovie(movieId)
    }
}