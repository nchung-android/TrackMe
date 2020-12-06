package com.nchungdev.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nchungdev.data.model.SessionEntity

@Database(
    entities = [SessionEntity::class],
    version = 12
)
@TypeConverters(value = [Converters::class])
abstract class TrackingDatabase : RoomDatabase() {
    abstract fun getRunDao(): SessionDAO

    companion object {

        private const val DATABASE_NAME = "trackme_db"

        @Volatile // All threads have immediate access to this property
        private var instance: TrackingDatabase? = null

        private val LOCK = Any() // Makes sure no threads making the same thing at the same time

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, TrackingDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }
    }
}