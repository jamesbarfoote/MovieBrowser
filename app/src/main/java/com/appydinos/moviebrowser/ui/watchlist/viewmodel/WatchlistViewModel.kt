package com.appydinos.moviebrowser.ui.watchlist.viewmodel

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.appydinos.moviebrowser.data.db.WatchlistItem
import com.appydinos.moviebrowser.data.repo.IWatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(private val watchlistRepository: IWatchlistRepository): ViewModel() {
    suspend fun deleteMovie(id: Int) = withContext(Dispatchers.IO) {
        watchlistRepository.deleteMovie(id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val watchList = MutableStateFlow<List<WatchlistItem>>(listOf())
        .flatMapLatest { watchlistRepository.getWatchlist() }
        .cachedIn(viewModelScope)

    val lazyGridState: LazyGridState = LazyGridState(0, 0)

}
