package com.appydinos.moviebrowser.ui.watchlist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.databinding.WatchlistItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.target.Target

/**
 * [RecyclerView.Adapter] that can display a watchlisted [Movie].
 */
class WatchlistRecyclerViewAdapter(
    binding: WatchlistItemBinding,
    private var onSelect: (Movie) -> Unit,
    private var onLongClick: (Movie) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val image = binding.moviePoster
    private val rating = binding.rating
    private val card = binding.movieCard

    companion object {
        fun create(parent: ViewGroup, onSelect: (Movie) -> Unit, onLongClick: (Movie) -> Unit): WatchlistRecyclerViewAdapter {
            val view = WatchlistItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return WatchlistRecyclerViewAdapter(view, onSelect, onLongClick)
        }
    }

    fun bind(movie: Movie) {
        Glide.with(image)
            .load(movie.posterURL)
            .transition(withCrossFade())
            .override(Target.SIZE_ORIGINAL)
            .into(image)

        card.setOnClickListener {
            onSelect(movie)
        }
        card.setOnLongClickListener {
            onLongClick(movie)
            true
        }
        if (movie.rating > 0) {
            rating.text = movie.rating.toString()
            rating.visibility = View.VISIBLE
        } else {
            rating.visibility = View.GONE
        }
    }
}
