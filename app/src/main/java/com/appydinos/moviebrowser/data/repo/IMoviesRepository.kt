package com.appydinos.moviebrowser.data.repo

import androidx.paging.PagingData
import com.appydinos.moviebrowser.data.model.Movie
import kotlinx.coroutines.flow.Flow

interface IMoviesRepository {
    fun getNowPlayingMovies(pageSize: Int): Flow<PagingData<Movie>>

    suspend fun getMovieDetails(movieId: Int): Movie?

}
