package com.appydinos.moviebrowser.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.databinding.FragmentSettingsBinding
import com.appydinos.moviebrowser.extensions.showShortToast
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.windowInsetTypesOf

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = FragmentSettingsBinding.inflate(inflater, container, false)

        view.tmdbLogo.setOnClickListener {
            try {
                val webpage: Uri = Uri.parse(getString(R.string.tmdp_url))
                val intent = Intent(Intent.ACTION_VIEW, webpage)
                startActivity(intent)
            } catch (ex: Exception) {
                context?.showShortToast("Failed to open your web browser")
            }
        }

        view.ossLicenses.setOnClickListener {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
        }

        Insetter.builder()
            .margin(windowInsetTypesOf(statusBars = true))
            .padding(windowInsetTypesOf(navigationBars = true))
            .applyToView(view.root)

        return view.root
    }
}
