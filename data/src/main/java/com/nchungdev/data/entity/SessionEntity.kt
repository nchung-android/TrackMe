package com.nchungdev.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nchungdev.domain.model.LocationModel
import com.nchungdev.domain.model.Polyline
import java.util.*

@Entity(tableName = "session_table")
data class SessionEntity(
    var img: String = "",
    var startLocation: LocationModel,
    var polylines: List<Polyline> = mutableListOf(mutableListOf()),
    var avgSpeedInKmph: Float = 0f,
    var distanceInKm: Float = 0f,
    var speedInKmph: Float = 0f,
    var timeInMillis: Long = 0L,
    var state: Int = SessionState.READY,
    var timestamp: Long = Calendar.getInstance().timeInMillis,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
