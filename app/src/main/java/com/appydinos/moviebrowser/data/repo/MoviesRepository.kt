package com.appydinos.moviebrowser.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.data.model.toMovie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MoviesRepository @Inject constructor(private val api: MovieService): IMoviesRepository {

    override fun getNowPlayingMovies(pageSize: Int): Flow<PagingData<Movie>> {
        return Pager(PagingConfig(pageSize)) { MoviePagingSource(api, null) }.flow
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
}
