package com.appydinos.moviebrowser.data.repo

import com.appydinos.moviebrowser.data.model.MovieListResponse
import com.appydinos.moviebrowser.data.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {

    @GET("movie/now_playing")
    suspend fun getLatestMovies(@Query("page") pageNumber: Int): Response<MovieListResponse?>

    @GET("movie/{id}")
    suspend fun getMovie(@Path("id") movieId: Int): Response<MovieResponse?>
}
