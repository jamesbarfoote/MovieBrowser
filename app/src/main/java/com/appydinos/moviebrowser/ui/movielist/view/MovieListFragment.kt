package com.appydinos.moviebrowser.ui.movielist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.databinding.FragmentMovieListBinding
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import com.appydinos.moviebrowser.ui.moviedetails.view.MovieDetailsFragment
import com.appydinos.moviebrowser.ui.movielist.viewmodel.MovieListViewModel
import com.appydinos.moviebrowser.ui.movielist.viewmodel.MoviesSlidingPaneViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * A fragment representing a list of Movies.
 */
@AndroidEntryPoint
class MovieListFragment : Fragment() {
    private val viewModel: MovieListViewModel by viewModels()
    private val movieSlidingPaneViewModel: MoviesSlidingPaneViewModel by activityViewModels()
    private lateinit var binding: FragmentMovieListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieListBinding.inflate(inflater)
        val listView = binding.listContent
        listView.setContent {
            MovieBrowserTheme(windows = null) {
                ListScreen(viewModel.lazyListState, viewModel.pagingData) { movieId ->
                    openDetails(movieId, binding = binding)
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                TwoPaneOnBackPressedCallback(binding.slidingPaneLayout)
            )
        }
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
