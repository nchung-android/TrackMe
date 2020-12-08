package com.nchungdev.data.db.mapper

import com.nchungdev.data.entity.LocationEntity
import com.nchungdev.domain.model.LocationModel

class LocationMapper : Mapper<LocationEntity, LocationModel> {
    override fun fromDTO(input: LocationEntity): LocationModel {
        return LocationModel(input.latitude, input.longitude)
    }

    override fun toDTO(input: LocationModel): LocationEntity {
        return LocationEntity(input.latitude, input.longitude)
    }
}