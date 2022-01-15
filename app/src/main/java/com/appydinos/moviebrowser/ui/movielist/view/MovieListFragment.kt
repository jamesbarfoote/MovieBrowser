package com.appydinos.moviebrowser.ui.movielist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.databinding.FragmentMovieListBinding
import com.appydinos.moviebrowser.ui.moviedetails.view.MovieDetailsFragment
import com.appydinos.moviebrowser.ui.movielist.adapter.MoviesAdapter
import com.appydinos.moviebrowser.ui.movielist.adapter.MoviesLoadStateAdapter
import com.appydinos.moviebrowser.ui.movielist.viewmodel.MovieListViewModel
import com.appydinos.moviebrowser.ui.movielist.viewmodel.MoviesSlidingPaneViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.windowInsetTypesOf
import kotlinx.coroutines.flow.collectLatest

/**
 * A fragment representing a list of Movies.
 */
@AndroidEntryPoint
class MovieListFragment : Fragment() {
    private val viewModel: MovieListViewModel by viewModels()
    private val movieSlidingPaneViewModel: MoviesSlidingPaneViewModel by activityViewModels()
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var binding: FragmentMovieListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieListBinding.inflate(inflater)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            TwoPaneOnBackPressedCallback(binding.slidingPaneLayout)
        )
        moviesAdapter = MoviesAdapter { selectedMovie ->
            openDetails(selectedMovie.id, binding)
        }

        binding.list.adapter =
            moviesAdapter.withLoadStateFooter(footer = MoviesLoadStateAdapter(moviesAdapter::retry))

        moviesAdapter.addLoadStateListener { loadStates ->
            if ((loadStates.source.refresh is LoadState.NotLoading || loadStates.refresh is LoadState.Loading) && moviesAdapter.itemCount == 0) {
                // Initial load so show the loader
                binding.lottieLoader.visibility = View.VISIBLE
            } else {
                // Hide the loader as we are either not loading or we have items in the list
                binding.lottieLoader.visibility = View.GONE
            }
            if (loadStates.refresh is LoadState.Error) {
                binding.errorView.visibility = View.VISIBLE
                binding.errorText.text = (loadStates.refresh as LoadState.Error).error.message
                binding.errorRetryButton.setOnClickListener { moviesAdapter.retry() }
            } else {
                binding.errorView.visibility = View.GONE
            }
        }
        setWindowInsets(binding.list)
        return binding.root
    }

    private fun openDetails(itemId: Int, binding: FragmentMovieListBinding) {
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.detail_container,
                MovieDetailsFragment::class.java,
                bundleOf("itemId" to itemId))
            if (binding.slidingPaneLayout.isOpen) {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            }
        }

        binding.slidingPaneLayout.open()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.slidingPaneLayout.doOnNextLayout {
            movieSlidingPaneViewModel.setIsTwoPane(!binding.slidingPaneLayout.isSlideable)
        }
        lifecycleScope.launchWhenCreated {
            viewModel.moviesList.collectLatest {
                moviesAdapter.submitData(it)
            }
        }
    }

    private fun setWindowInsets(v: View) {
        Insetter.builder()
            .paddingBottom(windowInsetTypesOf(navigationBars = true))
            .padding(windowInsetTypesOf(statusBars = true))
            .applyToView(v)
    }
}

class TwoPaneOnBackPressedCallback(
    private val slidingPaneLayout: SlidingPaneLayout
) : OnBackPressedCallback(slidingPaneLayout.isOpen), SlidingPaneLayout.PanelSlideListener {

    init {
        slidingPaneLayout.addPanelSlideListener(this)
    }

    override fun handleOnBackPressed() {
        slidingPaneLayout.closePane()
    }

    override fun onPanelSlide(panel: View, slideOffset: Float) {}

    override fun onPanelOpened(panel: View) {
        isEnabled = true
    }

    override fun onPanelClosed(panel: View) {
        isEnabled = false
    }
}
