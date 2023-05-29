package com.appydinos.moviebrowser.ui.moviedetails.viewmodel

import androidx.annotation.RawRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.data.model.Video
import com.appydinos.moviebrowser.data.repo.IWatchlistRepository
import com.appydinos.moviebrowser.data.repo.MoviesRepository
import com.appydinos.moviebrowser.ui.moviedetails.model.MessageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: MoviesRepository,
    private val watchlistRepository: IWatchlistRepository,
    @Named("ioDispatcher") private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie

    //Observables for updating the view state
    private val _showDetailsLoader = MutableStateFlow(true)
    val showDetailsLoader: StateFlow<Boolean> = _showDetailsLoader

    private val _messageState = MutableStateFlow(MessageState(
        showMessageView = false,
        messageText = "",
        messageAnimation = R.raw.details_error,
        animationAspectRatio = 1f,
        canRetry = false
    ))
    val messageState: StateFlow<MessageState> = _messageState

    private val _isInWatchlist = MutableStateFlow(false)
    val isInWatchlist: StateFlow<Boolean> = _isInWatchlist

    private val _videos = MutableStateFlow(listOf<Video>())

    fun getMovieDetails(movieId: Int) = viewModelScope.launch(ioDispatcher) {
        try {
            if (movieId < 0) {
                //Invalid movie ID so show error message
                showErrorView(errorMessage = "Select a movie to see its details", animation = R.raw.loader_movie, canRetry = false, 1f)
            } else {
                val result = repository.getMovieDetails(movieId)
                getMovieTrailers(movieId = movieId)

                if (result == null) {
                    showErrorView("Failed to get movie details", aspectRatio = 0.8f)
                } else {
                    _messageState.value = _messageState.value.copy(showMessageView = false)
                }
                _showDetailsLoader.value = false
                updateMovie(result)
            }
        } catch (ex: Exception) {
            showErrorView("Something went wrong when trying to get the movie details", aspectRatio = 0.8f)
            Timber.e(ex.message.orEmpty())
        }
    }

    private fun updateMovie(movie: Movie?) {
        _movie.value = if (_videos.value.isNotEmpty()) {
            //If the videos come back before the movie details then we need to update the movie
            movie?.copy(videos = _videos.value)
        } else {
            movie
        }
    }

    private fun getMovieTrailers(movieId: Int) = viewModelScope.launch(ioDispatcher) {
        val result = repository.getMovieVideos(movieId = movieId)
        _videos.value = result
        if (_movie.value != null) {
            //Update the movie object
            _movie.value = _movie.value?.copy(videos = result)
        }
    }

    private fun showErrorView(errorMessage: String, @RawRes animation: Int = R.raw.details_error, canRetry: Boolean = true, aspectRatio: Float) {
        _showDetailsLoader.value = false
        _messageState.value = _messageState.value.copy(
            showMessageView = true,
            messageText = errorMessage,
            messageAnimation = animation,
            animationAspectRatio = aspectRatio,
            canRetry = canRetry
        )
    }

    fun addToWatchList() {
        viewModelScope.launch(ioDispatcher) {
            _isInWatchlist.value = true
            movie.value?.let { watchlistRepository.addMovie(it) }
        }
    }

    suspend fun setMovie(movie: Movie) = withContext(ioDispatcher) {
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

    fun removeFromWatchlist() {
        viewModelScope.launch(ioDispatcher) {
            movie.value?.let {
                watchlistRepository.deleteMovie(it.id)
                _isInWatchlist.value = false
            }
        }
    }

    suspend fun checkIfInWatchlist(movieId: Int) = withContext(ioDispatcher) {
        val movie = watchlistRepository.getMovieDetails(movieId)
        _isInWatchlist.value = movie != null
    }
}
