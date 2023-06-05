package com.appydinos.moviebrowser.ui.movielist.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.appydinos.moviebrowser.data.repo.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(private val moviesRepository: MoviesRepository): ViewModel() {
    private var searchQuery = mutableStateOf("")
    fun search(searchString: String) {
        searchQuery.value = searchString
    }

    var pagingData = moviesRepository.searchNowPlayingMovies(30, searchQuery)
        .cachedIn(viewModelScope)

    val lazyListState: LazyListState = LazyListState()

}
