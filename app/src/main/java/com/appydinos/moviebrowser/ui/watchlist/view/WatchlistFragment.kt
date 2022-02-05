package com.appydinos.moviebrowser.ui.watchlist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.map
import com.appydinos.moviebrowser.databinding.FragmentWatchlistBinding
import com.appydinos.moviebrowser.ui.watchlist.adapter.MoviesLoadStateAdapter
import com.appydinos.moviebrowser.ui.watchlist.adapter.WatchlistAdapter
import com.appydinos.moviebrowser.ui.watchlist.viewmodel.WatchlistViewModel
import com.google.android.flexbox.*
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.Side
import dev.chrisbanes.insetter.windowInsetTypesOf
import kotlinx.coroutines.flow.collectLatest

/**
 * A simple screen that shows the users watch listed movies
 */
@AndroidEntryPoint
class WatchlistFragment : Fragment() {
    private val viewModel: WatchlistViewModel by viewModels()
    private lateinit var watchlistAdapter: WatchlistAdapter
    private var watchlistBottomSheet: WatchlistBottomSheet? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentWatchlistBinding.inflate(inflater, container, false)

        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        layoutManager.alignItems = AlignItems.CENTER
        layoutManager.flexWrap = FlexWrap.WRAP
        binding.list.layoutManager = layoutManager

        watchlistAdapter = WatchlistAdapter(onSelect = { selectedMovie ->
            findNavController().navigate(
                WatchlistFragmentDirections.actionWatchlistFragmentToMovieDetailsFragment(
                    movie = selectedMovie,
                    origin = "Watchlist"
                )
            )
        }) { selectedMovie ->
            watchlistBottomSheet = WatchlistBottomSheet.newInstance(selectedMovie.title) {
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    viewModel.deleteMovie(
                        selectedMovie.id
                    )
                }
            }.also {
                it.show(childFragmentManager, WatchlistBottomSheet.TAG)
            }
        }

        watchlistAdapter.addLoadStateListener { loadStates ->
            if ((loadStates.source.refresh is LoadState.NotLoading) && (loadStates.append.endOfPaginationReached) && watchlistAdapter.itemCount == 0) {
                binding.noWatchlistMovies.visibility = View.VISIBLE
            } else {
                binding.noWatchlistMovies.visibility = View.GONE
            }
        }

        binding.list.adapter =
            watchlistAdapter.withLoadStateFooter(footer = MoviesLoadStateAdapter(watchlistAdapter::retry))

        Insetter.builder()
            .margin(windowInsetTypesOf(statusBars = true))
            .padding(windowInsetTypesOf(navigationBars = true), sides = Side.RIGHT)
            .applyToView(binding.root)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.watchList.collectLatest {
                watchlistAdapter.submitData(it.map { watchlist -> watchlist.movie })
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (watchlistBottomSheet != null) {
            watchlistBottomSheet?.dismiss()
        }
    }
}
