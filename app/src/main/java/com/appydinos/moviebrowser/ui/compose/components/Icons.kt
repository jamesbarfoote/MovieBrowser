package com.appydinos.moviebrowser.ui.compose.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.appydinos.moviebrowser.R

@Composable
fun RotatingIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: Int,
    description: String,
    duration: Int = 1000,
    onClicked: () -> Unit
) {
    val isEnabled = remember { mutableStateOf(true) }
    val isRotated = remember { mutableStateOf(false) }

    val angle: Float by animateFloatAsState(
        targetValue = if (isRotated.value) 360F else 0F,
        animationSpec = tween(
            durationMillis = duration,
            easing = LinearEasing
        ),
        finishedListener = {
            isEnabled.value = true
        }
    )

    IconButton(modifier = modifier, onClick = {
        isRotated.value = !isRotated.value
        isEnabled.value = false
        onClicked()
    }, enabled = isEnabled.value) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = description,
            modifier = Modifier.rotate(angle)
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun RotatingIconPreview() {
    RotatingIcon(
        iconId = R.drawable.ic_add_to_watchlist,
        description = "Add to watchlist",
        onClicked = {}
    )
}