package com.nchungdev.data.util

import android.location.Location
import com.google.android.gms.tasks.Task
import com.nchungdev.domain.model.LocationModel
import kotlinx.coroutines.CompletableDeferred

suspend fun <T> Task<T>.asDeferred(): T {
    val deferred = CompletableDeferred<T>()

    this.addOnSuccessListener { result ->
        deferred.complete(result)
    }

    this.addOnFailureListener { exception ->
        deferred.completeExceptionally(exception)
    }

    return deferred.await()
}

fun Location.toModel() = LocationModel(latitude, longitude)