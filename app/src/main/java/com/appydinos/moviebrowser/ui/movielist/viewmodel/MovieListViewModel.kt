package com.appydinos.moviebrowser.ui.movielist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.appydinos.moviebrowser.data.repo.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(moviesRepository: MoviesRepository): ViewModel() {

    val pagingData = moviesRepository.getNowPlayingMovies(30)
        .cachedIn(viewModelScope)
}
