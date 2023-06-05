package com.appydinos.moviebrowser.data.repo

import androidx.compose.runtime.MutableState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.data.model.Video
import com.appydinos.moviebrowser.data.model.toMovie
import com.appydinos.moviebrowser.data.model.toVideoList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MoviesRepository @Inject constructor(
    private val api: MovieService): IMoviesRepository {

    override fun getNowPlayingMovies(pageSize: Int): Flow<PagingData<Movie>> {
        return Pager(PagingConfig(pageSize)) { MoviePagingSource(api, null) }.flow
    }

    override fun searchNowPlayingMovies(pageSize: Int, searchQuery: MutableState<String>?): Flow<PagingData<Movie>> {
        return Pager(PagingConfig(pageSize)) { MoviePagingSource(api, searchQuery) }.flow
    }

    @Throws
    override suspend fun getMovieDetails(movieId: Int): Movie? {
        val result = api.getMovie(movieId)
        if (result.isSuccessful) {
            result.body()?.let { movie ->
                return movie.toMovie()
            }
        }
        return null
    }

    @kotlin.jvm.Throws
    override suspend fun getMovieVideos(movieId: Int): List<Video> {
        val result = api.getVideos(movieId = movieId)
        if (result.isSuccessful) {
            result.body()?.let { videos ->
                return videos.toVideoList()
            }
        }
        return listOf()
    }
}
