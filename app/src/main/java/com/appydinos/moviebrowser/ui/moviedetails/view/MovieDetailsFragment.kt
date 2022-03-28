package com.appydinos.moviebrowser.ui.moviedetails.view

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.airbnb.lottie.compose.*
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.data.model.Video
import com.appydinos.moviebrowser.extensions.showShortToast
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import com.appydinos.moviebrowser.ui.compose.components.MessageView
import com.appydinos.moviebrowser.ui.compose.components.RotatingIcon
import com.appydinos.moviebrowser.ui.compose.components.RoundedCornerImage
import com.appydinos.moviebrowser.ui.moviedetails.viewmodel.MovieDetailsViewModel
import com.appydinos.moviebrowser.ui.movielist.viewmodel.MoviesSlidingPaneViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {
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
        val view = ComposeView(requireContext()).apply {
            setContent {
                MovieBrowserTheme(windows = activity?.window) {
                    ProvideWindowInsets {
                        DetailsScreen()
                    }
                }
            }
        }

        val movie = try {
            val args: MovieDetailsFragmentArgs by navArgs()
            isFromWatchlist = args.origin == "Watchlist"
            args.movie
        } catch (ex: java.lang.Exception) {
            null
        }
        if (movie == null) {
            //We are navigating from the movies list
            val movieId = getMovieId()
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.checkIfInWatchlist(movieId)
                viewModel.getMovieDetails(movieId)
            }
        } else {
            //We are navigating from the watchlist
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.checkIfInWatchlist(movie.id)
                viewModel.setMovie(movie)
            }
        }
        return view
    }

    private fun getMovieId(): Int {
        return arguments?.get("itemId") as? Int ?: -1
    }

    @Composable
    fun DetailsScreen() {
        val scrollState = rememberScrollState()
        val isInWatchlist by viewModel.isInWatchlist.collectAsState()
        val isTwoPane by movieSlidingPaneViewModel.isTwoPane.collectAsState()
        val movie by viewModel.movie.collectAsState()

        Scaffold(
            modifier = Modifier,
            topBar = {
                DetailsToolbar(isInWatchlist = isInWatchlist, isTwoPane = isTwoPane, movie = movie)
            }
        ) {
            val showLoader by viewModel.showDetailsLoader.collectAsState(true)
            if (showLoader) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ripple_loading))
                val progress by animateLottieCompositionAsState(
                    composition,
                    restartOnPlay = true,
                    iterations = LottieConstants.IterateForever,
                    speed = 0.5f
                )
                LottieAnimation(
                    composition,
                    progress,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(MaterialTheme.colors.surface)
                    .verticalScroll(scrollState)
                    .navigationBarsPadding()
                    .statusBarsPadding()
            ) {
                val showMessageView by viewModel.showMessageView.collectAsState(true)
                if (showMessageView) {
                    val messageAnimation by viewModel.messageAnimation.collectAsState()
                    val animationAspectRatio by viewModel.messageAnimationAspectRatio.collectAsState()
                    val messageText by viewModel.messageText.collectAsState()
                    val canRetry by viewModel.canRetry.collectAsState()

                    MessageView(
                        animation = messageAnimation,
                        animationAspectRation = animationAspectRatio,
                        messageText = messageText,
                        canRetry = canRetry
                    ) {
                        viewModel.getMovieDetails(getMovieId())
                    }
                }
                movie?.let { DetailsContent(movie = it) }
            }
        }
    }

    @Composable
    fun DetailsToolbar(isInWatchlist: Boolean, isTwoPane: Boolean, movie: Movie?) {
        TopAppBar(
            title = { Text(text = "Details", modifier = Modifier) },
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp,
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding(bottom = false)
                .padding(top = 5.dp),
            navigationIcon = {
                if (!isTwoPane || isFromWatchlist) {
                    IconButton(onClick = { activity?.onBackPressed() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                }
            },
            actions = {
                if (movie != null) {
                    RotatingIcon(
                        iconId = if (isInWatchlist) R.drawable.ic_remove_from_watchlist else R.drawable.ic_add_to_watchlist,
                        description = if (isInWatchlist) "Remove from watchlist" else "Add to watchlist",
                        onClicked = {
                            if (isInWatchlist) {
                                context?.showShortToast("Movie removed from Watchlist")
                                viewLifecycleOwner.lifecycleScope.launchWhenStarted { viewModel.removeFromWatchlist() }
                            } else {
                                context?.showShortToast("Movie added to Watchlist")
                                viewLifecycleOwner.lifecycleScope.launchWhenStarted { viewModel.addToWatchList() }
                            }
                        }
                    )
                }
            }
        )
    }

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun DetailsContent(movie: Movie) {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp)
            ) {

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
                RoundedCornerImage(
                    painter = painter,
                    height = 350.dp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = movie.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = movie.getInfoText(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = movie.tagLine,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.rating),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                    textAlign = TextAlign.Start
                )
                Text(
                    text = movie.getRatingText(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    )
                Text(
                    text = stringResource(id = R.string.overview),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                    textAlign = TextAlign.Start
                )
                Text(
                    text = movie.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    )

                Text(
                    text = stringResource(id = R.string.trailers),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                )
                Trailers(
                    videos = movie.videos,
                    movieTitle = movie.title
                )
            }
        }
    }

    @Composable
    fun Trailers(videos: List<Video>?, movieTitle: String) {
        val videoScrollState = rememberLazyListState()

        LazyRow(
            state = videoScrollState,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Spacer(modifier = Modifier.padding(start = 0.dp))
            }
            videos?.forEach { video ->
                item {
                    val videoPainter =
                        rememberImagePainter(data = video.thumbnail, builder = {
                            crossfade(true)
                        })
                    Image(
                        painter = videoPainter,
                        contentDescription = "$movieTitle trailer",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .clickable {
                                onTrailerClicked(video.url)
                            }
                            .clip(RoundedCornerShape(5.dp))
                            .width(256.dp)
                            .height(144.dp)
                            .aspectRatio(
                                ratio = 1.777f,
                                matchHeightConstraintsFirst = false
                            )
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.padding(end = 0.dp))
            }
        }
    }

    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, device = Devices.AUTOMOTIVE_1024p, widthDp = 1024, heightDp = 720)
    @Composable
    fun DetailsContentPreview() {
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
                DetailsContent(movie)
            }
        }
    }

    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, group = "Toolbar")
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, group = "Toolbar")
    @Composable
    fun ToolbarPreview() {
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
            DetailsToolbar(isInWatchlist = true, isTwoPane = false, movie)
        }
    }

    private fun onTrailerClicked(url: String) {
        try {
            val webpage: Uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(intent)
        } catch (ex: Exception) {
            context?.showShortToast("Failed to open trailer")
        }
    }
}
