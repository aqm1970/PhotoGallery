package com.bignerdranch.android.photogallery

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import java.lang.Exception

private const val TAG = "PollWorker"
class PollWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        val preferenceRepository = PreferenceRepository.get()
        val photoRepository = PhotoRepository()

        val query = preferenceRepository.storedQuery.first()
        val lastId = preferenceRepository.lastResultId.first()

        if (query.isEmpty()) {
            Log.i(TAG, "No saved query, finishing early.")
            return Result.success()
        }

        return try {
            val items = photoRepository.searchPhotos(query)

            if (items.isNotEmpty()) {
                val newResultsId = items.first().id
                if (newResultsId== lastId) {
                    Log.i(TAG, "Still have the same results: $newResultsId")
                } else {
                    Log.i(TAG, "Got a new result: $newResultsId")
                    preferenceRepository.setLastResultId(newResultsId)
                }
            }

             Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, "Background update failed", ex)
            Result.failure()
        }
    }
}