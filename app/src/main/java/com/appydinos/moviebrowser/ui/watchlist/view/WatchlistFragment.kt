package com.appydinos.moviebrowser.ui.watchlist.view

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.appydinos.moviebrowser.databinding.FragmentWatchlistBinding
import com.appydinos.moviebrowser.extensions.showShortToast
import com.appydinos.moviebrowser.ui.movielist.adapter.MoviesAdapter
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        binding.list.layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)

        watchlistAdapter = WatchlistAdapter(onSelect = { selectedMovie ->
            context?.showShortToast("Selected ${selectedMovie.title}")
            //TODO Go to movie details
        }) { selectedMovie ->
            WatchlistBottomSheet(selectedMovie.title) { viewModel.deleteMovie(selectedMovie.id) }
                .show(childFragmentManager, WatchlistBottomSheet.TAG)

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
}