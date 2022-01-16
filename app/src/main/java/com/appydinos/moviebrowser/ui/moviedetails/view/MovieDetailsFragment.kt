package com.appydinos.moviebrowser.ui.moviedetails.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.databinding.FragmentMovieDetailsBinding
import com.appydinos.moviebrowser.extensions.showShortToast
import com.appydinos.moviebrowser.ui.moviedetails.viewmodel.MovieDetailsViewModel
import com.appydinos.moviebrowser.ui.movielist.viewmodel.MoviesSlidingPaneViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.Side
import dev.chrisbanes.insetter.windowInsetTypesOf
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMovieDetailsBinding
    private val viewModel: MovieDetailsViewModel by viewModels()
    private val movieSlidingPaneViewModel: MoviesSlidingPaneViewModel by activityViewModels()
    private var isFromWatchlist: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.movieDetailsToolbar.title = "Details"

        binding.messageRetryButton.setOnClickListener {
            viewModel.getMovieDetails(getMovieId())
        }

        binding.movieDetailsToolbar.inflateMenu(R.menu.details_menu)
        binding.movieDetailsToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.add_to_watchlist -> {
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        viewModel.addToWatchList()
                    }
                    context?.showShortToast("Movie added to Watchlist")
                    true
                }
                R.id.remove_from_watchlist -> {
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        viewModel.removeFromWatchlist()
                    }
                    context?.showShortToast("Movie removed from Watchlist")
                    true
                }
                else -> {
                    false
                }
            }
        }

        Insetter.builder()
            .padding(windowInsetTypesOf(statusBars = true))
            .padding(windowInsetTypesOf(navigationBars = true), sides = Side.RIGHT)
            .applyToView(binding.root)

        val movie = try {
            val args: MovieDetailsFragmentArgs by navArgs()
            isFromWatchlist = args.origin == "Watchlist"
            args.movie
        } catch (ex: java.lang.Exception) {
            null
        }
        if (movie == null) {
            val movieId = getMovieId()
            monitorWatchlistStatus(movieId)
            viewModel.getMovieDetails(movieId)
        } else {
            monitorWatchlistStatus(movie.id)
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.setMovie(movie)
            }
        }
        return binding.root
    }

    private fun getMovieId(): Int {
        return arguments?.get("itemId") as? Int ?: -1
    }

    private fun monitorWatchlistStatus(movieId: Int) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isInWatchlist.collect { isInWatchlist ->
                    viewModel.checkIfInWatchlist(movieId)
                    //Add to watchlist menu item
                    binding.movieDetailsToolbar.menu.getItem(0).isVisible = !isInWatchlist
                    //Remove from watchlist menu item
                    binding.movieDetailsToolbar.menu.getItem(1).isVisible = isInWatchlist

                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressSpinner.visibility = View.VISIBLE
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movie.collect { movie ->
                    Glide.with(requireContext())
                        .load(movie?.posterURL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .override(Target.SIZE_ORIGINAL)
                        .addListener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                binding.progressSpinner.visibility = View.GONE
                                return false
                            }

                        })
                        .into(binding.moviePoster)

                    binding.movieTitle.text = movie?.getFullTitleText()
                    binding.movieDescription.text = movie?.description
                    binding.movieInfo.text = movie?.getInfoText()
                    if (movie?.tagLine.isNullOrBlank()) {
                        binding.movieTagline.visibility = View.GONE
                    } else {
                        binding.movieTagline.visibility = View.VISIBLE
                        binding.movieTagline.text = movie?.tagLine
                    }
                    binding.rating.text = movie?.getRatingText()
                }
            }
        }

        setNavIcon()
    }

    private fun setNavIcon() {
        lifecycleScope.launch {
            movieSlidingPaneViewModel.isTwoPane
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { isTwoPane ->
                    if (!isTwoPane || isFromWatchlist) {
                        binding.movieDetailsToolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
                        binding.movieDetailsToolbar.setNavigationOnClickListener {
                            requireActivity().onBackPressed()
                        }
                    } else {
                        binding.movieDetailsToolbar.navigationIcon = null
                        binding.movieDetailsToolbar.setNavigationOnClickListener(null)
                    }
                }
        }
    }
}
