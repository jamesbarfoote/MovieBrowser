package com.appydinos.moviebrowser.ui.movielist.view

import android.content.res.Configuration
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
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import com.appydinos.moviebrowser.ui.compose.components.FooterView
import com.appydinos.moviebrowser.ui.compose.components.LoadStateView
import com.appydinos.moviebrowser.ui.compose.components.RatingIcon
import com.appydinos.moviebrowser.ui.compose.components.RoundedCornerImage
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.flow.Flow

@Composable
fun ListScreen(
    state: LazyListState,
    flow: Flow<PagingData<Movie>>,
    onItemClicked: (movieId: Int) -> Unit) {
    //TODO Is the problem that we report a list size of 0 upon rotation?
    val listItems = flow.collectAsLazyPagingItems()
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = state,
        modifier = Modifier
            .statusBarsPadding()
    ) {

        items(items = listItems, key = { movie -> movie.id }, itemContent = { movie ->
            if (movie != null) {
                MovieListItem(movie = movie) {
                    onItemClicked(movie.id)
                }
            }
        })

        item {
            LoadStateView(
                loadState = listItems.loadState, movieCount = listItems.itemCount
            ) { listItems.retry() }
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
                    },
                    contentDescription = movie.title
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