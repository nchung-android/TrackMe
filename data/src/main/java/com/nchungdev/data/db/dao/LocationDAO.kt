package com.nchungdev.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nchungdev.data.entity.LocationEntity

@Dao
interface LocationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(locationEntity: LocationEntity)

    @Delete
    suspend fun delete(locationEntity: LocationEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(locationEntity: LocationEntity)

    @Query("SELECT * FROM location_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastLocationAsync(): LocationEntity?

    @Query("SELECT * FROM location_table ORDER BY id DESC LIMIT 1")
    fun getLastLocation(): LiveData<LocationEntity?>
}
