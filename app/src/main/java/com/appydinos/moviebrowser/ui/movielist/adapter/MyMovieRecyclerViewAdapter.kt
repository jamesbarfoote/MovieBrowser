package com.appydinos.moviebrowser.ui.movielist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appydinos.moviebrowser.data.model.Movie
import com.appydinos.moviebrowser.databinding.MovieItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.target.Target

/**
 * [RecyclerView.Adapter] that can display a [Movie].
 */
class MyMovieRecyclerViewAdapter(
    binding: MovieItemBinding,
    private var onSelect: (Movie) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val title = binding.movieTitle
    private val description = binding.movieDescription
    private val card = binding.movieCard
    private val image = binding.moviePoster
    private val rating = binding.rating

    companion object {
        fun create(parent: ViewGroup, onSelect: (Movie) -> Unit): MyMovieRecyclerViewAdapter {
            val view = MovieItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return MyMovieRecyclerViewAdapter(view, onSelect)
        }
    }

    fun bind(movie: Movie) {
        Glide.with(image)
            .load(movie.posterURL)
            .transition(withCrossFade())
            .override(Target.SIZE_ORIGINAL)
            .into(image)

        title.text = movie.title
        description.text = movie.description
        card.setOnClickListener {
            onSelect(movie)
        }
        if (movie.rating > 0) {
            rating.text = movie.rating.toString()
            rating.visibility = View.VISIBLE
        } else {
            rating.visibility = View.GONE
        }
    }
}
