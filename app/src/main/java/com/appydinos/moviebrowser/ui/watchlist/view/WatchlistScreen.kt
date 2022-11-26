package com.appydinos.moviebrowser.ui.watchlist.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberImagePainter
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.data.db.WatchlistItem
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.extensions.items
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import com.appydinos.moviebrowser.ui.compose.components.LoadStateView
import com.appydinos.moviebrowser.ui.compose.components.RatingIcon
import com.appydinos.moviebrowser.ui.compose.components.RoundedCornerImage
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private val selectedMovie: MutableState<Movie?> = mutableStateOf(null)
private val hasNoMovies: MutableState<Boolean> = mutableStateOf(true)

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun WatchlistScreen(
    watchList: Flow<PagingData<WatchlistItem>>,
    lazyGridState: LazyGridState,
    onDeleteMovie: (Movie) -> Unit,
    onMovieSelected: (Movie) -> Unit
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val coroutineScope = rememberCoroutineScope()
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            LongPressBottomSheetContent(
                bottomSheetScaffoldState = bottomSheetScaffoldState,
                coroutineScope = coroutineScope,
                onDeleteMovie = onDeleteMovie
            )
        },
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(
            topStart = 8.dp,
            topEnd = 8.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    ) {
        WatchlistContent(
            bottomSheetScaffoldState,
            coroutineScope,
            watchList,
            onMovieSelected = onMovieSelected,
            lazyGridState = lazyGridState
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun WatchlistContent(
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope,
    watchList: Flow<PagingData<WatchlistItem>>,
    lazyGridState: LazyGridState,
    onMovieSelected: (Movie) -> Unit
) {
    val lazyPagingItems = watchList.collectAsLazyPagingItems()

    //This workaround allows us to handle configuration changes without being spanned to the top
    //https://issuetracker.google.com/issues/177245496
    val refresh = lazyPagingItems.loadState.refresh
    val shouldSkipCurrentState = lazyPagingItems.itemCount == 0 && refresh is LoadState.NotLoading

    hasNoMovies.value =
        (lazyPagingItems.loadState.append.endOfPaginationReached && lazyPagingItems.itemCount == 0)

    if (hasNoMovies.value) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(id = R.string.no_watchlist_yet),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    } else if (!shouldSkipCurrentState) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(160.dp),
            state = lazyGridState,
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding(end = true, bottom = false),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = {

                items(lazyPagingItems) { item ->
                    if (item != null) {
                        PosterWithRating(
                            movie = item.movie,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.65f, true),
                            onClick = {
                                onMovieSelected(it)
                            },
                            onLongClick = {
                                //Show bottom sheet
                                selectedMovie.value = it
                                coroutineScope.launch {
                                    if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                                        bottomSheetScaffoldState.bottomSheetState.expand()
                                    } else {
                                        bottomSheetScaffoldState.bottomSheetState.collapse()
                                    }
                                }
                            })
                    }
                }

                item {
                    LoadStateView(
                        loadState = lazyPagingItems.loadState,
                        movieCount = lazyPagingItems.itemCount
                    ) { lazyPagingItems.retry() }
                }
            })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PosterWithRating(
    movie: Movie,
    modifier: Modifier,
    onClick: (Movie) -> Unit,
    onLongClick: (Movie) -> Unit
) {
    val painter = rememberImagePainter(data = movie.posterURL, builder = {
        crossfade(true)
    })

    ConstraintLayout(modifier = modifier) {
        val (image, rating) = createRefs()

        RoundedCornerImage(
            painter = painter,
            height = 300.dp,
            modifier = Modifier
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .combinedClickable(
                    onClick = { onClick(movie) },
                    onLongClick = { onLongClick(movie) }
                ),
            contentDescription = movie.title
        )
        RatingIcon(rating = movie.rating, modifier = Modifier.constrainAs(rating) {
            start.linkTo(image.start, margin = 4.dp)
            bottom.linkTo(image.bottom, margin = 8.dp)
        })
    }
}

@Preview(showBackground = true)
@Composable
fun PosterPreview() {
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
        PosterWithRating(
            movie = movie,
            modifier = Modifier.background(Color.Blue),
            onClick = {},
            onLongClick = {})
    }
}

@ExperimentalMaterialApi
@Composable
fun LongPressBottomSheetContent(
    bottomSheetScaffoldState: BottomSheetScaffoldState?,
    coroutineScope: CoroutineScope?,
    onDeleteMovie: (Movie) -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .navigationBarsPadding(end = true, bottom = false)
    ) {
        Column {
            Text(
                text = selectedMovie.value?.title.orEmpty(),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("Bottom sheet title")
            )
            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())

            //Delete movie from watchlist
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF5252)),
                onClick = {
                    selectedMovie.value?.let { movie -> onDeleteMovie(movie) }
                    coroutineScope?.launch {
                        bottomSheetScaffoldState?.bottomSheetState?.collapse()
                    }
                }) {
                Text(
                    text = stringResource(id = R.string.delete),
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun LongPressBottomSheetContentPreview() {
    selectedMovie.value = Movie(
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
    LongPressBottomSheetContent(null, null) {}
}
