package com.appydinos.moviebrowser.ui.movielist.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * A Shared ViewModel between the list and details.
 * This enables us to know the current state of the sliding panel
 */
@HiltViewModel
class MoviesSlidingPaneViewModel @Inject constructor(): ViewModel() {

    private val _isTwoPane = MutableStateFlow(true)
    val isTwoPane: StateFlow<Boolean> = _isTwoPane

    fun setIsTwoPane(isTwoPane: Boolean) {
        _isTwoPane.value = isTwoPane
    }
}
