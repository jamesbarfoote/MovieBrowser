package com.appydinos.moviebrowser.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.extensions.showShortToast
import com.appydinos.moviebrowser.ui.compose.MovieBrowserTheme
import com.google.accompanist.insets.ProvideWindowInsets
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
