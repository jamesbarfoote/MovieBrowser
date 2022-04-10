package com.appydinos.moviebrowser.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Video(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String,
    val url: String,
    val thumbnail: String
): Parcelable

fun VideoResponse.toVideoList(): List<Video> {
    val videos = arrayListOf<Video>()
    this.results.forEach { result ->
        videos.add(
            Video(
                id = result.id,
                key = result.key,
                name = result.name,
                site = result.site,
                type = result.type,
                url = "https://www.youtube.com/watch?v=${result.key}",
                thumbnail = "https://img.youtube.com/vi/${result.key}/0.jpg"
            )
        )
    }
    return videos
}
