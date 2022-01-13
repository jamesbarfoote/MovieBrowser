package com.appydinos.moviebrowser.data.dp

import androidx.room.*
import com.appydinos.moviebrowser.data.model.Movie
import java.util.*

@Entity(tableName = "Watchlist")
data class WatchlistItem(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @Embedded val movie: Movie,
    @ColumnInfo(name = "added_at") val addedAt: Date?
)
