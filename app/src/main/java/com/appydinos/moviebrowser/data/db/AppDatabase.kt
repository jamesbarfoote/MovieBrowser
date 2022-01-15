package com.appydinos.moviebrowser.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [WatchlistItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder<AppDatabase>(context.applicationContext,
                    AppDatabase::class.java, "item_database")
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build()
            }
        }
    }
}
