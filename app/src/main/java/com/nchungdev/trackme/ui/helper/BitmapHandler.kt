package com.nchungdev.trackme.ui.helper

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import com.nchungdev.domain.util.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import javax.inject.Inject

class BitmapHandler @Inject constructor(
    private val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {

    suspend fun save(bitmap: Bitmap?, id: Int): Uri {
        bitmap ?: return Uri.EMPTY
        return try {
            withContext(dispatcher) {
                bitmapToFile(
                    context.applicationContext,
                    bitmap,
                    id
                )
            }
        } catch (e: Exception) {
            return Uri.EMPTY
        }
    }

    // Method to save an bitmap to a file
    private fun bitmapToFile(context: Context, bitmap: Bitmap, id: Int): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(context.applicationContext)
        // Initialize a new file instance to save bitmap object
        val file = File(wrapper.cacheDir, "$id.jpg")
        try {
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath)
    }
}