package com.nchungdev.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nchungdev.domain.model.LocationModel
import com.nchungdev.domain.model.Polyline
import java.util.*

@Entity(tableName = "session_table")
data class SessionEntity(
    var imgPath: String? = "",
    var startLocation: LocationModel? = LocationModel(),
    var polylines: List<Polyline>? = mutableListOf(mutableListOf()),
    var avgSpeedInKmph: Float? = 0F,
    var distanceInKm: Float? = 0F,
    var speedInKmph: Float? = 0F,
    var timeInMillis: Long? = 0L,
    var state: Int? = SessionState.NOT_RUNNING,
    var timestamp: Long? = Calendar.getInstance().timeInMillis,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
