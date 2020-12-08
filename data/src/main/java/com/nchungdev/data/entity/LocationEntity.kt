package com.nchungdev.data.entity

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_table")
class LocationEntity(
    var latitude: Double? = 0.0,
    var longitude: Double? = 0.0,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    constructor(location: Location) : this(location.latitude, location.longitude)
}