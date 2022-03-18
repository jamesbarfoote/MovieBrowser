package com.appydinos.moviebrowser.data.db

import androidx.room.TypeConverter
import com.appydinos.moviebrowser.data.model.Video
import com.google.gson.Gson
import java.util.*


class Converters {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return if (dateLong == null) null else Date(dateLong)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun listToJson(value: List<String>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun videoListToJson(value: List<Video>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<String>? {
        val objects = Gson().fromJson(value, Array<String>::class.java)
        if (objects != null) {
            return objects.toList()
        }
        return listOf()
    }

    @TypeConverter
    fun jsonToVideoList(value: String): List<Video>? {
        val objects = Gson().fromJson(value, Array<Video>::class.java)
        if (objects != null) {
            return objects.toList()
        }
        return listOf()
    }
}
