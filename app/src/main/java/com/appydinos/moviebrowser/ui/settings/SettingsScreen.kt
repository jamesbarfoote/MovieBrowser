package com.appydinos.moviebrowser.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun SettingsContent(onLogoClicked: () -> Unit, onOSSClicked: () -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(scrollState)
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Image(painter = painterResource(id = R.drawable.ic_tmdb_icon),
            contentDescription = "TMDB Logo",
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onLogoClicked() }
                .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
        Text(
            text = stringResource(id = R.string.data_from_tmdb),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .testTag("All data text")
        )
        Button(
            onClick = { onOSSClicked() },
            modifier = Modifier.padding(top = 24.dp).testTag("OSS Licenses Button")) {
            Text(stringResource(id = R.string.oss_licenses))
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsContentPreview() {
    MovieBrowserTheme(windows = null) {
        ProvideWindowInsets {
            SettingsContent({}, {})
        }
    }
}