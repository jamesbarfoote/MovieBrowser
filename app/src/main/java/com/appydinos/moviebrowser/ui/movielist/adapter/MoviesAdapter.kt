package com.appydinos.moviebrowser.ui.movielist.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.appydinos.moviebrowser.data.model.Movie

class MoviesAdapter(private var onSelect: (Movie) -> Unit): PagingDataAdapter<Movie, MyMovieRecyclerViewAdapter>(
    MOVIE_COMPARATOR
) {

    override fun onBindViewHolder(holder: MyMovieRecyclerViewAdapter, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onBindViewHolder(
        holder: MyMovieRecyclerViewAdapter,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMovieRecyclerViewAdapter {
        return MyMovieRecyclerViewAdapter.create(parent, onSelect)
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