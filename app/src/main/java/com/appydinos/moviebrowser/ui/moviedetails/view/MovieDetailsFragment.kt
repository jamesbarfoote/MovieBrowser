package com.appydinos.moviebrowser.ui.moviedetails.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.appydinos.moviebrowser.extensions.showShortToast
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import com.appydinos.moviebrowser.ui.moviedetails.viewmodel.MovieDetailsViewModel
import com.appydinos.moviebrowser.ui.movielist.viewmodel.MoviesSlidingPaneViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {
    private val viewModel: MovieDetailsViewModel by viewModels()
    private val movieSlidingPaneViewModel: MoviesSlidingPaneViewModel by activityViewModels()
    private var isFromWatchlist: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val movie = try {
            val args: MovieDetailsFragmentArgs by navArgs()
            isFromWatchlist = args.origin == "Watchlist"
            args.movie
        } catch (ex: java.lang.Exception) {
            null
        }

        val view = ComposeView(requireContext()).apply {
            setContent {
                val scope = rememberCoroutineScope()
                MovieBrowserTheme(windows = activity?.window) {
                    ProvideWindowInsets {
                        DetailsScreen(
                            inWatchlist = viewModel.isInWatchlist,
                            twoPane = movieSlidingPaneViewModel.isTwoPane,
                            currentMovie = viewModel.movie,
                            shouldShowLoader = viewModel.showDetailsLoader,
                            messageState = viewModel.messageState,
                            isFromWatchlist = isFromWatchlist,
                            onBackPressed = { activity?.onBackPressed() },
                            onTrailerClicked = { url -> onTrailerClicked(url) },
                            removeFromWatchlist = {
                                context?.showShortToast("Movie removed from Watchlist")
                                viewModel.removeFromWatchlist()
                            },
                            addToWatchlist = {
                                context?.showShortToast("Movie added to Watchlist")
//                                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                                    viewModel.addToWatchList()
//                                }
                            },
                            onLoadRetry = { viewModel.getMovieDetails(getMovieId()) }
                        )
                    }
                }
            }
        }

        if (movie == null) {
            //We are navigating from the movies list
            val movieId = getMovieId()
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.checkIfInWatchlist(movieId)
                    viewModel.getMovieDetails(movieId)
                }
            }
        } else {
            //We are navigating from the watchlist
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.checkIfInWatchlist(movie.id)
                    viewModel.setMovie(movie)
                }
            }
        }
        return view
    }

    private fun getMovieId(): Int {
        return arguments?.get("itemId") as? Int ?: -1
    }

    private fun onTrailerClicked(url: String) {
        try {
            val webpage: Uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(intent)
        } catch (ex: Exception) {
            context?.showShortToast("Failed to open trailer")
        }
    }
}
