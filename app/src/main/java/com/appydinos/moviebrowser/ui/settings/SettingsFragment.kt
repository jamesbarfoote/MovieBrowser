package com.appydinos.moviebrowser.ui.settings

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.extensions.showShortToast
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext()).apply {
            setContent {
                MovieBrowserTheme(windows = activity?.window) {
                    ProvideWindowInsets {
                        SettingsContent(
                            onLogoClicked = { onTMDBLogoClicked() },
                            onOSSClicked = {
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        OssLicensesMenuActivity::class.java
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
        return view
    }

    private fun onTMDBLogoClicked() {
        try {
            val webpage: Uri = Uri.parse(getString(R.string.tmdb_url))
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(intent)
        } catch (ex: Exception) {
            context?.showShortToast("Failed to open your web browser")
        }
    }
}

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
        )
        Button(onClick = { onOSSClicked() }, modifier = Modifier.padding(top = 24.dp)) {
            Text(stringResource(id = R.string.oss_licenses))
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SettingsContentPreview() {
    MovieBrowserTheme(windows = null) {
        ProvideWindowInsets {
            SettingsContent({}, {})
        }
    }
}
