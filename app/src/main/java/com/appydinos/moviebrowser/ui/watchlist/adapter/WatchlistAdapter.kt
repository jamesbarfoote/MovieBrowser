package com.appydinos.moviebrowser.ui.watchlist.adapter

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.appydinos.moviebrowser.data.model.Movie

class WatchlistAdapter(
    private var onSelect: (Movie, View, Int) -> Unit,
    private var onLongClick: (Movie) -> Unit
) : PagingDataAdapter<Movie, WatchlistRecyclerViewAdapter>(MOVIE_COMPARATOR) {

    override fun onBindViewHolder(holder: WatchlistRecyclerViewAdapter, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onBindViewHolder(
        holder: WatchlistRecyclerViewAdapter,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            getItem(position)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WatchlistRecyclerViewAdapter {
        return WatchlistRecyclerViewAdapter.create(parent, onSelect, onLongClick)
    }

    companion object {
        val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<Movie>() {
            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem.id == newItem.id
        }
    }
}
