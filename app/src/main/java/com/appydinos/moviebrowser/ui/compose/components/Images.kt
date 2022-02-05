package com.appydinos.moviebrowser.ui.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundedCornerImage(painter: Painter, height: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .clip(
                RoundedCornerShape(8.dp)
            )
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .height(height),
            contentScale = ContentScale.FillHeight
        )
    }
}