package com.appydinos.moviebrowser.ui.moviedetails.view

import android.content.res.Configuration
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.airbnb.lottie.compose.*
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.data.model.Video
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import com.appydinos.moviebrowser.ui.compose.components.MessageView
import com.appydinos.moviebrowser.ui.compose.components.RotatingIcon
import com.appydinos.moviebrowser.ui.compose.components.RoundedCornerImage
import com.appydinos.moviebrowser.ui.moviedetails.model.MessageState
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DetailsScreen(
    inWatchlist: StateFlow<Boolean>,
    twoPane: StateFlow<Boolean>,
    currentMovie: StateFlow<Movie?>,
    shouldShowLoader: StateFlow<Boolean>,
    messageState: StateFlow<MessageState>,
    isFromWatchlist: Boolean,
    onBackPressed: () -> Unit,
    onTrailerClicked: (String) -> Unit,
    removeFromWatchlist: () -> Unit,
    addToWatchlist: () -> Unit,
    onLoadRetry: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isInWatchlist by inWatchlist.collectAsState()
    val isTwoPane by twoPane.collectAsState()
    val movie by currentMovie.collectAsState()

    Scaffold(
        modifier = Modifier,
        topBar = {
            DetailsToolbar(
                isInWatchlist = isInWatchlist,
                isTwoPane = isTwoPane,
                movie = movie,
                onBackPressed = onBackPressed,
                removeFromWatchlist = removeFromWatchlist,
                addToWatchlist = addToWatchlist,
                isFromWatchlist = isFromWatchlist
            )
        }
    ) {
        val showLoader by shouldShowLoader.collectAsState(true)
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
            val currentMessageState by messageState.collectAsState()
            if (currentMessageState.showMessageView) {
                MessageView(
                    animation = currentMessageState.messageAnimation,
                    animationAspectRation = currentMessageState.animationAspectRatio,
                    messageText = currentMessageState.messageText,
                    canRetry = currentMessageState.canRetry,
                    onRetry = onLoadRetry
                )
            }
            movie?.let { DetailsContent(movie = it, onTrailerClicked) }
        }
    }
}

@Composable
fun DetailsToolbar(
    isInWatchlist: Boolean,
    isTwoPane: Boolean,
    movie: Movie?,
    isFromWatchlist: Boolean,
    onBackPressed: () -> Unit,
    removeFromWatchlist: () -> Unit,
    addToWatchlist: () -> Unit) {
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
                IconButton(onClick = { onBackPressed() }) {
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
                            removeFromWatchlist()
                        } else {
                            addToWatchlist()
                        }
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun DetailsContent(movie: Movie, onTrailerClicked: (String) -> Unit) {
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
                movieTitle = movie.title,
                onTrailerClicked = onTrailerClicked
            )
        }
    }
}

@Composable
fun Trailers(videos: List<Video>?, movieTitle: String, onTrailerClicked: (String) -> Unit) {
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
            DetailsContent(movie) {}
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
        DetailsToolbar(
            isInWatchlist = true,
            isTwoPane = false,
            movie = movie,
            isFromWatchlist = false,
            onBackPressed = {},
            removeFromWatchlist = {},
            addToWatchlist = {}
        )
    }
}

