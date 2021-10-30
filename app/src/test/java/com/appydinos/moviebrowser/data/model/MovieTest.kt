package com.appydinos.moviebrowser.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class MovieTest {

    @Test
    fun `getFullTitleText - returns title correctly`() {
        //When
        val title = testMovie.getFullTitleText()

        //Then
        assertEquals("Red Notice (2021)", title)
    }

    @Test
    fun `getFullTitleText - returns title correctly when release date is invalid`() {
        //When
        val title = testMovie.copy(releaseDate = "invalid").getFullTitleText()

        //Then
        assertEquals("Red Notice ", title)
    }

    @Test
    fun `getInfoText - returns info correctly`() {
        //When
        val info = testMovie.getInfoText()

        //Then
        assertEquals("1h 37m | 2021-11-04 | Action, Comedy", info)
    }

    @Test
    fun `getInfoText - returns info correctly when one genre`() {
        //When
        val info = testMovie.copy(genre = listOf("Action")).getInfoText()

        //Then
        assertEquals("1h 37m | 2021-11-04 | Action", info)
    }

    @Test
    fun `getInfoText - returns info correctly when no genre`() {
        //When
        val info = testMovie.copy(genre = null).getInfoText()

        //Then
        assertEquals("1h 37m | 2021-11-04 ", info)
    }

    @Test
    fun `getRatingText - returns rating`() {
        //When
        val rating = testMovie.getRatingText()

        //Then
        assertEquals("6.8 (1388 votes)", rating)
    }

    @Test
    fun `toMovie - converts movie response to movie`() {
        //Given
        val expectedMovie = Movie(
            id = 2,
            title = "Free Guy",
            description = "A bank teller called Guy realizes...",
            posterURL = "https://image.tmdb.org/t/p/w500/freeguy.img",
            releaseDate = "2021-08-11",
            rating = 7.8,
            genre = listOf("Comedy", "Adventure"),
            runTime = "1h 55m",
            status = "Released",
            tagLine = "Life's too short to be a background character.",
            votes = 4038
        )

        //When
        val result = movieResponse.toMovie()

        //Then
        assertEquals(expectedMovie, result)
    }

    @Test
    fun `toMovie - converts movie list response to movie`() {
        //Given
        val expectedMovie = Movie(
            id = 2,
            title = "Free Guy",
            description = "A bank teller called Guy realizes...",
            posterURL = "https://image.tmdb.org/t/p/w500/freeguy.img",
            releaseDate = "2021-08-11",
            rating = 7.8,
            votes = 4038
        )

        //When
        val result = movieListResponse.toMovie()

        //Then
        assertEquals(expectedMovie, result)
    }
}

val testMovie = Movie(
    id = 1,
    title = "Red Notice",
    description = "An interpol issued Red Notice is...",
    posterURL = "themoviedb.org/abc123",
    releaseDate = "2021-11-04",
    rating = 6.8,
    genre = listOf("Action", "Comedy"),
    runTime = "1h 37m",
    status = "Released",
    tagLine = "Pro and cons",
    votes = 1388
)

val freeGuyMovie = Movie(
    id = 2,
    title ="Free Guy",
    description =  "A bank teller called Guy realizes...",
    posterURL = "https://image.tmdb.org/t/p/w500/freeguy.img",
    releaseDate = "2021-08-11",
    rating = 7.8,
    genre = listOf("Comedy", "Adventure"),
    runTime = "1h 55m",
    status = "Released",
    tagLine =  "Life's too short to be a background character.",
    votes = 4038
)

val freeGuyMovieList = Movie(
    id = 2,
    title ="Free Guy",
    description =  "A bank teller called Guy realizes...",
    posterURL = "https://image.tmdb.org/t/p/w500/freeguy.img",
    releaseDate = "2021-08-11",
    rating = 7.8,
    genre = null,
    runTime = "",
    status = "",
    votes = 4038
)

val movieResponse = MovieResponse(
    adult = false,
    backdropPath = "backdrop",
    belongsToCollection = MovieResponse.BelongsToCollection(
        backdropPath = "path",
        id = 0,
        name = "",
        posterPath = ""
    ),
    budget = 365897870,
    genres = listOf(MovieResponse.Genre(1, "Comedy"), MovieResponse.Genre(2, "Adventure")),
    homepage = "home",
    id = 2,
    imdbId = "",
    originalLanguage = "",
    originalTitle = "",
    overview = "A bank teller called Guy realizes...",
    popularity = 0.0,
    posterPath = "/freeguy.img",
    productionCompanies = listOf(),
    productionCountries = listOf(),
    releaseDate = "2021-08-11",
    revenue = 0,
    runtime = 115,
    spokenLanguages = listOf(),
    status = "Released",
    tagline = "Life's too short to be a background character.",
    title = "Free Guy",
    video = false,
    voteAverage = 7.8,
    voteCount = 4038
)

val movieListResponse = MovieListResponse.Result(
    adult = false,
    backdropPath = "backdrop",
    id = 2,
    genreIds = listOf(1, 2),
    originalLanguage = "",
    originalTitle = "",
    overview = "A bank teller called Guy realizes...",
    popularity = 0.0,
    posterPath = "/freeguy.img",
    releaseDate = "2021-08-11",
    title = "Free Guy",
    video = false,
    voteAverage = 7.8,
    voteCount = 4038
)
