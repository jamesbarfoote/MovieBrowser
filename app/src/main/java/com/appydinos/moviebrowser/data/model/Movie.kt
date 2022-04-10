package com.appydinos.moviebrowser.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.appydinos.moviebrowser.extensions.toHoursAndMinutes
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import java.time.LocalDate

private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500"

@Parcelize
data class Movie(
    @ColumnInfo(name = "movie_id") val id: Int,
    val title: String,
    val description: String,
    val posterURL: String,
    val releaseDate: String,
    val rating: Double,
    val genre: List<String> = arrayListOf(),
    val runTime: String = "",
    val status: String = "",
    val tagLine: String = "",
    val votes: Int,
    var videos: List<Video>? = null
): Parcelable {
    fun getFullTitleText(): String {
        val year = try {
            "(${ LocalDate.parse(releaseDate).year })"
        } catch (ex: Exception) {
            Timber.e(ex)
            ""
        }
        return "$title $year"
    }

    fun getInfoText(): String {
        val genreText = if (!genre.isNullOrEmpty()) genre.let { "| ${ it.joinToString(", ") }" } else ""
        return "$runTime | $releaseDate $genreText"
    }

    fun getRatingText(): String {
        return "$rating ($votes votes)"
    }
}

fun MovieResponse.toMovie(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        description = this.overview,
        posterURL = "${POSTER_BASE_URL}${this.posterPath}",
        releaseDate = this.releaseDate,
        rating = this.voteAverage,
        genre = this.genres.map { it.name },
        runTime = this.runtime.toHoursAndMinutes(),
        status = this.status,
        tagLine = this.tagline,
        votes = this.voteCount
    )
}

fun MovieListResponse.Result.toMovie(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        description = this.overview,
        posterURL = "${POSTER_BASE_URL}${this.posterPath}",
        releaseDate = this.releaseDate,
        rating = this.voteAverage,
        votes = this.voteCount
    )
}
