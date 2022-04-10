package com.appydinos.moviebrowser.ui.watchlist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import com.appydinos.moviebrowser.ui.watchlist.viewmodel.WatchlistViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple screen that shows the users watch listed movies
 */
@AndroidEntryPoint
class WatchlistFragment : Fragment() {
    private val viewModel: WatchlistViewModel by viewModels()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MovieBrowserTheme(windows = null) {
                    ProvideWindowInsets {
                        WatchlistScreen(
                            watchList = viewModel.watchList,
                            lazyGridState = viewModel.lazyGridState,
                            onDeleteMovie = { selectedMovie ->
                                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                                    viewModel.deleteMovie(
                                        selectedMovie.id
                                    )
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
}
