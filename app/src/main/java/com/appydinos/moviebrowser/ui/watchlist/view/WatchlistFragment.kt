package com.appydinos.moviebrowser.ui.watchlist.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appydinos.moviebrowser.databinding.FragmentWatchlistBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.windowInsetTypesOf

/**
 * A simple screen that shows the users watch listed items
 */
@AndroidEntryPoint
class WatchlistFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentWatchlistBinding.inflate(inflater, container, false)

        Insetter.builder()
            .margin(windowInsetTypesOf(statusBars = true))
            .padding(windowInsetTypesOf(navigationBars = false))
            .paddingRight(windowInsetTypesOf(navigationBars = false))
            .applyToView(binding.root)

        return binding.root
    }
}