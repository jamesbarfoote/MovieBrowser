package com.appydinos.moviebrowser.ui.moviedetails.view

import android.content.Intent
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
import androidx.navigation.fragment.findNavController
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
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.windowInsetTypesOf
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMovieDetailsBinding
    private val viewModel: MovieDetailsViewModel by viewModels()
    private val movieSlidingPaneViewModel: MoviesSlidingPaneViewModel by activityViewModels()

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

        binding.movieDetailsToolbar.inflateMenu(R.menu.details_menu)
        binding.movieDetailsToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.oss_licenses -> {
                    startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
                    true
                }
                R.id.add_to_watchlist -> {
                    viewModel.addToWatchList()
                    context?.showShortToast("Movie added to Watchlist")
                    true
                }
                else -> {
                    false
                }
            }
        }
        binding.messageRetryButton.setOnClickListener {
            viewModel.getMovieDetails(getMovieId())
        }

        Insetter.builder()
            .margin(windowInsetTypesOf(statusBars = true))
            .padding(windowInsetTypesOf(navigationBars = false))
            .paddingRight(windowInsetTypesOf(navigationBars = false))
            .applyToView(binding.root)

        viewModel.getMovieDetails(getMovieId())
        return binding.root
    }

    private fun getMovieId(): Int {
        return try {
            val args: MovieDetailsFragmentArgs by navArgs()
            args.id
        } catch (ex: Exception) {
            -1
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
                        .addListener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable>?,
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
                    if (!isTwoPane) {
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
