package com.nchungdev.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nchungdev.domain.model.LocationModel
import com.nchungdev.domain.model.Polyline

class Converters {

    companion object {
        @TypeConverter
        @JvmStatic
        fun toLocationModel(jsonValue: String): LocationModel = Gson().fromJson(jsonValue, LocationModel::class.java)

        @TypeConverter
        @JvmStatic
        fun fromLocationModel(locationModel: LocationModel): String {
            return Gson().toJson(locationModel)
        }

        @TypeConverter
        @JvmStatic
        fun toLocationModelList(jsonValue: String): List<Polyline> {
            val type = object : TypeToken<List<Polyline>>() {}.type
            return Gson().fromJson(jsonValue, type)
        }

        @TypeConverter
        @JvmStatic
        fun fromLocationModelList(locationModel: List<Polyline>): String {
            val type = object : TypeToken<List<Polyline>>() {}.type
            return Gson().toJson(locationModel, type)
        }
    }
}