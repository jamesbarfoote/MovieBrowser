package com.appydinos.moviebrowser.ui.watchlist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import com.appydinos.moviebrowser.ui.watchlist.viewmodel.WatchlistViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * A simple screen that shows the users watch listed movies
 */
@AndroidEntryPoint
class WatchlistFragment : Fragment() {
    private val viewModel: WatchlistViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MovieBrowserTheme(windows = null) {
                    WatchlistScreen(
                        watchList = viewModel.watchList,
                        lazyGridState = viewModel.lazyGridState,
                        onDeleteMovie = { selectedMovie ->
                            viewLifecycleOwner.lifecycleScope.launch {
                                repeatOnLifecycle(Lifecycle.State.STARTED) {
                                    viewModel.deleteMovie(
                                        selectedMovie.id
                                    )
                                }
                            }
                        },
                        onMovieSelected = { movie ->
                            findNavController().navigate(
                                WatchlistFragmentDirections.actionWatchlistFragmentToMovieDetailsFragment(
                                    movie = movie,
                                    origin = "Watchlist"
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}
