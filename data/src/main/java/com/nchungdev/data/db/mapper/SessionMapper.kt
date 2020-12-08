package com.nchungdev.data.db.mapper

import com.nchungdev.data.entity.SessionEntity
import com.nchungdev.domain.model.SessionModel
import javax.inject.Inject

class SessionMapper @Inject constructor() : Mapper<SessionEntity, SessionModel> {

    override fun fromDTO(input: SessionEntity) = SessionModel(
        id = input.id ?: -1,
        imgPath = input.img,
        timestamp = input.timestamp,
        startLocation = input.startLocation,
        polylines = input.polylines,
        speedInKmph = input.speedInKmph,
        avgSpeedInKmph = input.avgSpeedInKmph,
        distanceInKm = input.distanceInKm,
        timeInMillis = input.timeInMillis,
        state = input.state
    )

    override fun toDTO(input: SessionModel) = SessionEntity(
        img = input.imgPath,
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
