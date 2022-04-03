package com.appydinos.moviebrowser.ui.compose.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.appydinos.moviebrowser.R
import timber.log.Timber

@Composable
fun LoadStateView(loadState: CombinedLoadStates, movieCount: Int, onRetry: () -> Unit) {
    if ((loadState.source.refresh is LoadState.NotLoading || loadState.refresh is LoadState.Loading) && movieCount == 0) {
        // Initial load so show the loader
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ripple_loading))
        val progress by animateLottieCompositionAsState(
            composition,
            restartOnPlay = true,
            iterations = 1,
            speed = 0.5f
        )
        LottieAnimation(
            composition,
            progress,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .aspectRatio(1F, true)
                .wrapContentHeight(Alignment.CenterVertically)
                .testTag("Footer Animation")
        )
    }
    if (loadState.append == LoadState.Loading) {
        FooterView(isLoading = true, "") {}
    }

    if (loadState.refresh is LoadState.Error && !loadState.append.endOfPaginationReached && movieCount == 0) {
        //Initial load failed
        val error = (loadState.refresh as? LoadState.Error)?.error?.message
        MessageView(modifier = Modifier, animation = R.raw.details_error, messageText = error.orEmpty(), canRetry = true, animationAspectRation = 0.8f) {
            onRetry()
        }
    } else if (loadState.refresh is LoadState.Error || loadState.append is LoadState.Error) {
        val message = ((loadState.refresh as? LoadState.Error) ?: (loadState.append as? LoadState.Error))?.error?.message
        Timber.v("Error: $message")
        FooterView(isLoading = false, errorMessage = message.orEmpty()) {
            onRetry()
        }
    }
}

@Composable
fun FooterView(isLoading: Boolean, errorMessage: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        } else {
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = errorMessage, color = Color.Red, modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(end = 8.dp)
                )
                Button(onClick = onRetry, modifier = Modifier.wrapContentSize()) {
                    Text(text = "Retry")
                }
            }
        }
    }
}

