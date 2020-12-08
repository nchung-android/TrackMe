package com.nchungdev.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nchungdev.data.db.dao.LocationDAO
import com.nchungdev.data.db.dao.SessionDAO
import com.nchungdev.data.entity.LocationEntity
import com.nchungdev.data.entity.SessionEntity

@Database(
    entities = [SessionEntity::class, LocationEntity::class],
    version = 15
)
@TypeConverters(value = [Converters::class])
abstract class TrackingDatabase : RoomDatabase() {
    abstract fun getSessionDao(): SessionDAO
    abstract fun getLocationDao(): LocationDAO

    companion object {

        private const val DATABASE_NAME = "tracking_db"

        @Volatile // All threads have immediate access to this property
        private var instance: TrackingDatabase? = null

        private val LOCK = Any() // Makes sure no threads making the same thing at the same time

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TrackingDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }
    }
}
