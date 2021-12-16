package com.appydinos.moviebrowser.ui.movielist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.databinding.FragmentMovieListBinding
import com.appydinos.moviebrowser.ui.movielist.adapter.MoviesAdapter
import com.appydinos.moviebrowser.ui.movielist.adapter.MoviesLoadStateAdapter
import com.appydinos.moviebrowser.ui.movielist.viewmodel.MovieListViewModel
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
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var binding: FragmentMovieListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieListBinding.inflate(inflater)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            TwoPaneOnBackPressedCallback(binding.slidingPaneLayout))
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
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.detail_container) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(
            R.id.movieDetailsFragment,
            bundleOf("id" to itemId),
            NavOptions.Builder()
                // Pop all destinations off the back stack.
                .setPopUpTo(navController.graph.startDestination, true)
                .apply {
                    // If we're already open and the detail pane is visible,
                    // crossfade between the destinations.
                    if (binding.slidingPaneLayout.isOpen) {
                        setEnterAnim(androidx.navigation.ui.R.animator.nav_default_enter_anim)
                        setExitAnim(androidx.navigation.ui.R.animator.nav_default_exit_anim)
                    }
                }
                .build()
        )
        binding.slidingPaneLayout.open()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenCreated {
            viewModel.moviesList.collectLatest {
                moviesAdapter.submitData(it)
            }
        }
    }

    private fun setWindowInsets(v: View) {
        Insetter.builder()
            .padding(windowInsetTypesOf(navigationBars = true))
            .padding(windowInsetTypesOf(statusBars = true))
            .applyToView(v)
    }
}

class TwoPaneOnBackPressedCallback(
    private val slidingPaneLayout: SlidingPaneLayout
) : OnBackPressedCallback(slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen), SlidingPaneLayout.PanelSlideListener {

    init {
        slidingPaneLayout.addPanelSlideListener(this)
    }

    override fun handleOnBackPressed() {
        slidingPaneLayout.closePane()
    }

    override fun onPanelSlide(panel: View, slideOffset: Float) { }

    override fun onPanelOpened(panel: View) {
        isEnabled = true
    }

    override fun onPanelClosed(panel: View) {
        isEnabled = false
    }
}
