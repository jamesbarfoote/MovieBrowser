package com.appydinos.moviebrowser.ui.moviedetails.view

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.databinding.FragmentMovieDetailsBinding
import com.appydinos.moviebrowser.ui.moviedetails.viewmodel.MovieDetailsViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMovieDetailsBinding
    private val viewModel: MovieDetailsViewModel by viewModels()

    private val args: MovieDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentMovieDetailsBinding.inflate(inflater)
        binding.movieDetailsToolbar.title = "Details"
        binding.movieDetailsToolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
        binding.movieDetailsToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.movieDetailsToolbar.inflateMenu(R.menu.details_menu)
        binding.movieDetailsToolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.oss_licenses -> {
                    startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
                    true
                }
                else -> { false }
            }
        }

        viewModel.getMovieDetails(args.id)
        return binding.root
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
                    if (movie != null) {
                        binding.lottieLoader.visibility = View.GONE
                        binding.errorView.visibility = View.GONE
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorText.collectLatest { errorText ->
                    if (errorText != null) {
                        binding.errorText.text = errorText
                        binding.errorView.visibility = View.VISIBLE
                        binding.lottieLoader.visibility = View.GONE
                        binding.errorRetryButton.setOnClickListener {
                            viewModel.getMovieDetails(args.id)
                        }
                    }
                }
            }
        }
    }
}
