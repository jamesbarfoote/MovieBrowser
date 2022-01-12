package com.appydinos.moviebrowser.ui.watchlist.viewmodel

import androidx.lifecycle.ViewModel
import com.appydinos.moviebrowser.data.repo.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(private val repository: MoviesRepository): ViewModel() {

}