package com.appydinos.moviebrowser.data.repo

import androidx.compose.runtime.MutableState
import androidx.paging.PagingData
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.data.model.Video
import kotlinx.coroutines.flow.Flow

interface IMoviesRepository {
    fun getNowPlayingMovies(pageSize: Int): Flow<PagingData<Movie>>

    fun searchNowPlayingMovies(pageSize: Int, searchQuery: MutableState<String>?): Flow<PagingData<Movie>>

    suspend fun getMovieDetails(movieId: Int): Movie?

    suspend fun getMovieVideos(movieId: Int): List<Video>
}
