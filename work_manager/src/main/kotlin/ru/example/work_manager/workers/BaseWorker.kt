package ru.example.work_manager.workers

import android.app.NotificationManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

sealed class BaseWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    protected val notificationManager = context.getSystemService(NotificationManager::class.java)

    protected companion object {
        const val NOTIFICATION_ID = 69
    }
}
