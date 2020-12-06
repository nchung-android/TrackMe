package com.nchungdev.data.db.mapper

import com.google.android.gms.maps.model.LatLng
import com.nchungdev.domain.model.LocationModel

interface Mapper<DTO, D> {

    fun fromDTO(input: DTO): D

    fun toDTO(input: D): DTO
}

interface ListMapper<I, O> : Mapper<List<I>, List<O>>

fun LocationModel.toLatLng() = LatLng(latitude, longitude)