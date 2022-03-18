package com.appydinos.moviebrowser.ui.moviedetails.viewmodel

import androidx.annotation.RawRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.data.model.Video
import com.appydinos.moviebrowser.data.repo.IWatchlistRepository
import com.appydinos.moviebrowser.data.repo.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: MoviesRepository,
    private val watchlistRepository: IWatchlistRepository
) : ViewModel() {

    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie

    //Observables for updating the view state
    private val _showDetailsLoader = MutableStateFlow(true)
    val showDetailsLoader: StateFlow<Boolean> = _showDetailsLoader


    private val _showMessageView = MutableStateFlow(false)
    val showMessageView: StateFlow<Boolean> = _showMessageView

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText

    private val _messageAnimation = MutableStateFlow(R.raw.details_error)
    val messageAnimation: StateFlow<Int> = _messageAnimation

    private val _canRetry = MutableStateFlow(false)
    val canRetry: StateFlow<Boolean> = _canRetry

    private val _messageAnimationAspectRatio = MutableStateFlow(1f)
    val messageAnimationAspectRatio: StateFlow<Float> = _messageAnimationAspectRatio

    private val _isInWatchlist = MutableStateFlow(false)
    val isInWatchlist: StateFlow<Boolean> = _isInWatchlist

    private val _videos = MutableStateFlow(listOf<Video>())

    fun getMovieDetails(movieId: Int) = viewModelScope.launch {
        try {
            if (movieId < 0) {
                //Invalid movie ID so show error message
                showErrorView(errorMessage = "Select a movie to see its details", animation = R.raw.loader_movie, canRetry = false, 1f)
            } else {
                getMovieTrailers(movieId = movieId)
                val result = repository.getMovieDetails(movieId)

                if (result == null) {
                    showErrorView("Failed to get movie details", aspectRatio = 0.8f)
                } else {
                    _showMessageView.value = false
                }
                _showDetailsLoader.value = false
                _movie.value = result
            }
        } catch (ex: Exception) {
            showErrorView("Something went wrong when trying to get the movie details", aspectRatio = 0.8f)
            Timber.e(ex.message.orEmpty())
        }
    }

    private fun getMovieTrailers(movieId: Int) = viewModelScope.launch {
        val result = repository.getMovieVideos(movieId = movieId)
        _videos.value = result
        if (_movie.value != null) {
            //Update the movie object
            _movie.value = _movie.value?.copy(videos = result)
        }
    }

    private fun showErrorView(errorMessage: String, @RawRes animation: Int = R.raw.details_error, canRetry: Boolean = true, aspectRatio: Float) {
        _showDetailsLoader.value = false
        _showMessageView.value = true
        _messageText.value = errorMessage
        _messageAnimation.value = animation
        _messageAnimationAspectRatio.value = aspectRatio
        _canRetry.value = canRetry
    }

    suspend fun addToWatchList() = withContext(Dispatchers.IO) {
        _isInWatchlist.value = true
        movie.value?.let { watchlistRepository.addMovie(it) }
    }

    suspend fun setMovie(movie: Movie) = withContext(Dispatchers.IO) {
        if (movie.videos.isNullOrEmpty()) {
            getMovieTrailers(movieId = movie.id).invokeOnCompletion {
                viewModelScope.launch(Dispatchers.IO) {
                    _movie.value?.let { thisMovie -> watchlistRepository.updateTrailers(thisMovie) }
                }
            }
        }
        _showDetailsLoader.value = false
        _movie.value = movie
        checkIfInWatchlist(movieId = movie.id)
    }

    suspend fun removeFromWatchlist() = withContext(Dispatchers.IO) {
        movie.value?.let {
            watchlistRepository.deleteMovie(it.id)
            _isInWatchlist.value = false
        }
    }

    suspend fun checkIfInWatchlist(movieId: Int) = withContext(Dispatchers.IO) {
        val movie = watchlistRepository.getMovieDetails(movieId)
        _isInWatchlist.value = movie != null
    }
}
