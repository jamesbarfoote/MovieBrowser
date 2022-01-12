package com.appydinos.moviebrowser.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.windowInsetTypesOf
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationRail?.let {
            Insetter.builder()
                .margin(windowInsetTypesOf(navigationBars = true))
                .margin(windowInsetTypesOf(statusBars = true))
                .applyToView(it)
        }

        val navListener = NavigationBarView.OnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_movies -> {
                    Timber.v("navigation_movies")
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.movieListFragment)
                    true
                }
                R.id.navigation_watchlist -> {
                    Timber.v("navigation_watchlist")
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.watchlistFragment)
                    true
                }
                R.id.navigation_settings -> {
                    Timber.v("navigation_settings")
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.movieListFragment)
                    true
                }
                else -> false
            }

        }
        binding.navigationRail?.setOnItemSelectedListener(navListener)
        binding.bottomNavigation?.setOnItemSelectedListener(navListener)
    }
}
