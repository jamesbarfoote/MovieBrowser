package com.appydinos.moviebrowser.ui.watchlist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appydinos.moviebrowser.R
import com.appydinos.moviebrowser.databinding.WatchlistBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WatchlistBottomSheet : BottomSheetDialogFragment() {

    private var movieTitle: String = ""
    private var onDelete: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = WatchlistBottomSheetBinding.inflate(inflater, container, false)
        view.title.text = movieTitle
        view.delete.setOnClickListener {
            onDelete()
            dismiss()
        }
        return view.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    companion object {
        const val TAG = "WatchlistModalBottomSheet"

        fun newInstance(movieTitle: String, onDelete: () -> Unit): WatchlistBottomSheet {
            val fragment = WatchlistBottomSheet()
            fragment.movieTitle = movieTitle
            fragment.onDelete = onDelete
            return fragment
        }
    }
}
