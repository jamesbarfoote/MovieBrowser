package com.appydinos.moviebrowser.ui.movielist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.paging.LoadState
import com.appydinos.moviebrowser.databinding.FragmentMovieListBinding
import com.appydinos.moviebrowser.ui.movielist.adapter.MoviesAdapter
import com.appydinos.moviebrowser.ui.movielist.adapter.MoviesLoadStateAdapter
import com.appydinos.moviebrowser.ui.movielist.viewmodel.MovieListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * A fragment representing a list of Movies.
 */
@AndroidEntryPoint
class MovieListFragment : Fragment() {
    private val viewModel: MovieListViewModel by viewModels()
    private lateinit var moviesAdapter: MoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMovieListBinding.inflate(inflater)
        moviesAdapter = MoviesAdapter { selectedMovie ->
            findNavController(this).navigate(MovieListFragmentDirections.movieListFragmentToMovieDetailsFragment(selectedMovie.id))
        }
        binding.list.adapter = moviesAdapter.withLoadStateFooter(footer = MoviesLoadStateAdapter(moviesAdapter::retry))

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenCreated {
            viewModel.moviesList.collectLatest {
                moviesAdapter.submitData(it)
            }
        }
    }
}
