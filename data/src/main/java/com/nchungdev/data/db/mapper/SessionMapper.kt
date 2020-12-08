package com.nchungdev.data.db.mapper

import com.nchungdev.data.entity.SessionEntity
import com.nchungdev.data.entity.SessionState
import com.nchungdev.domain.model.LocationModel
import com.nchungdev.domain.model.SessionModel
import javax.inject.Inject

class SessionMapper @Inject constructor() : Mapper<SessionEntity, SessionModel> {

    override fun fromDTO(input: SessionEntity) = SessionModel(
        id = input.id ?: -1,
        imgPath = input.imgPath ?: "",
        timestamp = input.timestamp ?: 0L,
        startLocation = input.startLocation ?: LocationModel(),
        polylines = input.polylines ?: mutableListOf(kotlin.collections.mutableListOf()),
        speedInKmph = input.speedInKmph ?: 0F,
        avgSpeedInKmph = input.avgSpeedInKmph ?: 0F,
        distanceInKm = input.distanceInKm ?: 0F,
        timeInMillis = input.timeInMillis ?: 0L,
        state = input.state ?: SessionState.NOT_RUNNING
    )

    override fun toDTO(input: SessionModel) = SessionEntity(
        imgPath = input.imgPath,
        startLocation = input.startLocation,
        polylines = input.polylines,
        avgSpeedInKmph = input.avgSpeedInKmph,
        distanceInKm = input.distanceInKm,
        speedInKmph = input.speedInKmph,
        timeInMillis = input.timeInMillis,
        state = input.state,
        timestamp = input.timestamp
    ).apply {
        id = input.id
    }
}
