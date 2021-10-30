package com.appydinos.moviebrowser.data.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.data.model.MovieListResponse
import com.appydinos.moviebrowser.data.model.toMovie
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class MoviePagingSource(private val backendService: MovieService, private val query: String?): PagingSource<Int, Movie>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Movie> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = backendService.getLatestMovies(nextPageNumber)
            if (response.isSuccessful) {
                val data = response.body()
                LoadResult.Page(
                    data = movieResultsPageToMovieList(data?.results),
                    prevKey = null, // Only paging forward.
                    nextKey = if (data?.page == data?.totalPages) null else (data?.page?.plus(1))
                )
            } else {
                Timber.e("Failed to get latest movies. ${response.raw()}")
                LoadResult.Error(Exception(response.errorBody()?.string()))
            }
        } catch (e: IOException) {
            Timber.e("Load list failed. $e")
            LoadResult.Error(e)
        } catch (e: HttpException) {
            Timber.e("Load list failed. $e")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private fun movieResultsPageToMovieList(results: List<MovieListResponse.Result>?): List<Movie> {
        val movies: ArrayList<Movie> = arrayListOf()
        results?.forEach { movie ->
            movies.add(movie.toMovie())
        }
        return movies
    }
}