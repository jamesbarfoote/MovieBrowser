package com.appydinos.moviebrowser.ui.compose.components

import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme

@Composable
fun MessageView(
    modifier: Modifier = Modifier,
    @RawRes animation: Int,
    animationAspectRation: Float = 1f,
    messageText: String,
    canRetry: Boolean,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animation))
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
                .fillMaxWidth(0.5f)
                .wrapContentHeight()
                .aspectRatio(animationAspectRation, false)
        )

        Text(
            text = messageText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colors.onBackground
        )
        if (canRetry) {
            Button(onClick = { onRetry() }) {
                Text(text = stringResource(id = R.string.retry))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageViewPreview() {
    MovieBrowserTheme(windows = null) {
        MessageView(animation = R.raw.loader_movie, messageText = "Some message text here", canRetry = true, onRetry = {})
    }
}

@Composable
fun RatingIcon(modifier: Modifier = Modifier, rating: Double) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFF80072257))
            .border(width = 2.dp, color = Color(0xFF00E676), shape = CircleShape)
            .padding(4.dp)
    ) {
        Text(
            text = rating.toString(),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(4.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun RatingIconPreview() {
    RatingIcon(rating = 9.6)
}