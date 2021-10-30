package com.appydinos.moviebrowser.ui.moviedetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.data.repo.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(private val repository: MoviesRepository) : ViewModel() {
    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie
    private val _errorText = MutableStateFlow<String?>(null)
    val errorText: StateFlow<String?> = _errorText

    fun getMovieDetails(movieId: Int) = viewModelScope.launch {
        try {
            val result = repository.getMovieDetails(movieId)
            if (result == null) {
                _errorText.value = "Failed to get movie details"
            } else {
                _errorText.value = null
            }
            _movie.value = result
        } catch (ex: Exception) {
            _errorText.value = "Failed to get movie details"
            Timber.e(ex.message.orEmpty())
        }
    }
}
