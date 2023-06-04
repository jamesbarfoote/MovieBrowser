package com.appydinos.moviebrowser.ui.movielist.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import com.appydinos.moviebrowser.ui.compose.components.FooterView
import com.appydinos.moviebrowser.ui.compose.components.LoadStateView
import com.appydinos.moviebrowser.ui.compose.components.RatingIcon
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    state: LazyListState,
    flow: Flow<PagingData<Movie>>,
    onItemClicked: (movieId: Int) -> Unit) {

    val listItems = flow.collectAsLazyPagingItems()

    //This workaround allows us to handle configuration changes without being spanned to the top
    //https://issuetracker.google.com/issues/177245496
    val refresh = listItems.loadState.refresh
    if (listItems.itemCount == 0 && refresh is LoadState.NotLoading ) return //skip dummy state, waiting next compose

    MovieListView(state =state, listItems = listItems, onItemClicked = { onItemClicked(it) })
}

@Composable
fun MovieListView(state: LazyListState, listItems: LazyPagingItems<Movie>, onItemClicked: (movieId: Int) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = state,
        modifier = Modifier.statusBarsPadding()
    ) {

        items(
            count = listItems.itemCount,
            key = listItems.itemKey(key = { movie -> movie.id }),
            contentType = listItems.itemContentType()
        ) { index ->
            val item = listItems[index]
            if (item != null) {
                MovieListItem(movie = item) {
                    onItemClicked(item.id)
                }
            }
        }

        item {
            LoadStateView(
                loadState = listItems.loadState, movieCount = listItems.itemCount
            ) { listItems.retry() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListItem(movie: Movie, onClick: (Movie) -> Unit) {
    ElevatedCard(onClick = { onClick(movie) }) {
        Column(modifier = Modifier.padding(16.dp)) {
            ConstraintLayout(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                val (image, rating) = createRefs()

                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(movie.posterURL)
                        .crossfade(true)
                        .build(),
                    loading = {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .height(50.dp)
                                .width(50.dp)
                                .padding(top = 175.dp)
                        )
                    },
                    contentDescription = movie.title,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .height(300.dp)
                        .constrainAs(image) {
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
