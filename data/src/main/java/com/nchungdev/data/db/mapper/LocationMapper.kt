package com.nchungdev.data.db.mapper

import com.nchungdev.data.entity.LocationEntity
import com.nchungdev.domain.model.LocationModel

class LocationMapper : Mapper<LocationEntity, LocationModel> {
    override fun fromDTO(input: LocationEntity) =
        LocationModel(input.latitude ?: 0.0, input.longitude ?: 0.0)

    override fun toDTO(input: LocationModel) =
        LocationEntity(input.latitude, input.longitude)
}