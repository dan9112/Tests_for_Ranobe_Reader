package ru.example.work_manager.workers

import android.content.Context
import androidx.work.WorkerParameters

class ResetWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {
    override suspend fun doWork(): Result {
        notificationManager.cancel(NOTIFICATION_ID)
        return Result.success()
    }
}
