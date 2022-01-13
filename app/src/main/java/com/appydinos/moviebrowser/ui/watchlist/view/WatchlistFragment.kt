package com.appydinos.moviebrowser.ui.watchlist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appydinos.moviebrowser.databinding.FragmentWatchlistBinding
import com.appydinos.moviebrowser.ui.movielist.adapter.MoviesLoadStateAdapter
import com.appydinos.moviebrowser.ui.watchlist.adapter.WatchlistAdapter
import com.appydinos.moviebrowser.ui.watchlist.viewmodel.WatchlistViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.windowInsetTypesOf
import kotlinx.coroutines.flow.collectLatest

/**
 * A simple screen that shows the users watch listed items
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
        binding.list.layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)

        watchlistAdapter = WatchlistAdapter(onSelect = { selectedMovie ->
            findNavController().navigate(WatchlistFragmentDirections.actionWatchlistFragmentToMovieDetailsFragment(movie = selectedMovie, origin = "Watchlist"))
        }) { selectedMovie ->
            watchlistBottomSheet = WatchlistBottomSheet.newInstance(selectedMovie.title) { viewModel.deleteMovie(selectedMovie.id) }.also {
                it.show(childFragmentManager, WatchlistBottomSheet.TAG)
            }
        }
        binding.list.adapter =
            watchlistAdapter.withLoadStateFooter(footer = MoviesLoadStateAdapter(watchlistAdapter::retry))

        Insetter.builder()
            .margin(windowInsetTypesOf(statusBars = true))
            .padding(windowInsetTypesOf(navigationBars = false))
            .paddingRight(windowInsetTypesOf(navigationBars = false))
            .applyToView(binding.root)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenCreated {
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
