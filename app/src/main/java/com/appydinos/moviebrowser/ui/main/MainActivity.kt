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

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val LAST_SELECTED_ITEM_KEY = "MALSIK13012022"
    }

    private var currentDestination: Int = R.id.navigation_movies

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val savedSelectionId = savedInstanceState?.getInt(LAST_SELECTED_ITEM_KEY, -1)
        currentDestination = if (savedSelectionId != null && savedSelectionId >= 0) savedSelectionId else currentDestination
        binding.navigationRail?.selectedItemId = currentDestination
        binding.bottomNavigation?.selectedItemId = currentDestination

        binding.navigationRail?.let {
            Insetter.builder()
                .margin(windowInsetTypesOf(navigationBars = true))
                .margin(windowInsetTypesOf(statusBars = true))
                .applyToView(it)
        }

        val navListener = NavigationBarView.OnItemSelectedListener { item ->
            currentDestination = item.itemId
            when(item.itemId) {
                R.id.navigation_movies -> {
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.movieListFragment)
                    true
                }
                R.id.navigation_watchlist -> {
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.watchlistFragment)
                    true
                }
                R.id.navigation_settings -> {
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.settingsFragment)
                    true
                }
                else -> false
            }

        }
        binding.navigationRail?.setOnItemSelectedListener(navListener)
        binding.bottomNavigation?.setOnItemSelectedListener(navListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(LAST_SELECTED_ITEM_KEY, currentDestination)
    }
}
