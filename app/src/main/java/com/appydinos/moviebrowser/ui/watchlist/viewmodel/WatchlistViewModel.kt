package com.appydinos.moviebrowser.ui.watchlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.appydinos.moviebrowser.data.dp.WatchlistItem
import com.appydinos.moviebrowser.data.repo.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(private val watchlistRepository: WatchlistRepository): ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val watchList = MutableStateFlow<List<WatchlistItem>>(listOf())
        .flatMapLatest { watchlistRepository.getWatchlist() }
        .cachedIn(viewModelScope)
}