package com.appydinos.moviebrowser.ui.movielist.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.*
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.databinding.FragmentMovieListBinding
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import com.appydinos.moviebrowser.ui.compose.components.FooterView
import com.appydinos.moviebrowser.ui.compose.components.LoadStateView
import com.appydinos.moviebrowser.ui.compose.components.RatingIcon
import com.appydinos.moviebrowser.ui.compose.components.RoundedCornerImage
import com.appydinos.moviebrowser.ui.moviedetails.view.MovieDetailsFragment
import com.appydinos.moviebrowser.ui.movielist.viewmodel.MovieListViewModel
import com.appydinos.moviebrowser.ui.movielist.viewmodel.MoviesSlidingPaneViewModel
import com.google.accompanist.insets.ProvideWindowInsets
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
                ProvideWindowInsets {
                    Content(viewModel.lazyListState, viewModel.pagingData.collectAsLazyPagingItems())
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

    @Composable
    fun Content(state: LazyListState, flow: LazyPagingItems<Movie>) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = state,
            modifier = Modifier
                .statusBarsPadding()
        ) {

            items(items = flow, key = { movie -> movie.id }, itemContent = { movie ->
                if (movie != null) {
                    MovieListItem(movie = movie) {
                        openDetails(movie.id, binding)
                    }
                }
            })

            item {
                LoadStateView(
                    loadState = flow.loadState, movieCount = flow.itemCount
                ) { flow.retry() }
            }
        }
    }

    @OptIn(ExperimentalCoilApi::class, ExperimentalMaterialApi::class)
    @Composable
    fun MovieListItem(movie: Movie, onClick: (Movie) -> Unit) {
        Card(onClick = { onClick(movie) }) {
            Column(modifier = Modifier.padding(16.dp)) {
                val painter = rememberImagePainter(data = movie.posterURL, builder = {
                    crossfade(true)
                })
                if (painter.state is ImagePainter.State.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .height(50.dp)
                            .width(50.dp)
                            .padding(top = 175.dp)
                    )
                }
                ConstraintLayout(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    val (image, rating) = createRefs()

                    RoundedCornerImage(
                        painter = painter,
                        height = 300.dp,
                        modifier = Modifier.constrainAs(image) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )
                    RatingIcon(rating = movie.rating, modifier = Modifier.constrainAs(rating) {
                        start.linkTo(image.start, margin = 4.dp)
                        bottom.linkTo(image.bottom, margin = 8.dp)
                    })
                }
                Text(movie.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp))
                Text(movie.description, fontSize = 14.sp, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun FooterPreview() {
        FooterView(isLoading = false, errorMessage = "This is a long error message so that we can see what it looks like when it wraps") {}
    }

    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, device = Devices.AUTOMOTIVE_1024p, widthDp = 1024, heightDp = 720)
    @Composable
    fun MovieContentPreview() {
        val movie = Movie(
            id = 2,
            title = "Free Guy",
            description = "A bank teller called Guy realizes...",
            posterURL = "https://image.tmdb.org/t/p/w500/freeguy.img",
            releaseDate = "2021-08-11",
            rating = 7.8,
            genre = listOf("Comedy", "Adventure"),
            runTime = "1h 55m",
            status = "Released",
            tagLine = "Life's too short to be a background character.",
            votes = 4038
        )

        MovieBrowserTheme(windows = null) {
            ProvideWindowInsets {
                MovieListItem(movie) {}
            }
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
