package com.appydinos.moviebrowser.ui.movielist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.data.repo.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository): ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val moviesList = MutableStateFlow<List<Movie>>(listOf())
        .flatMapLatest { moviesRepository.getNowPlayingMovies(30) }
        .cachedIn(viewModelScope)
}
