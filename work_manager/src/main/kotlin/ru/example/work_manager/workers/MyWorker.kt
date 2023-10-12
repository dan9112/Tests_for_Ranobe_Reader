package ru.example.work_manager.workers

import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.work.*
import ru.example.work_manager.R
import ru.example.work_manager.util.getDelayInSeconds
import ru.example.work_manager.workers.MyWorker.Companion.NotificationsInfo.CHANNEL_IMPORTANCE
import ru.example.work_manager.workers.MyWorker.Companion.NotificationsInfo.ChannelDescriptionRes
import ru.example.work_manager.workers.MyWorker.Companion.NotificationsInfo.ChannelIdRes
import ru.example.work_manager.workers.MyWorker.Companion.NotificationsInfo.ChannelNameRes
import ru.example.work_manager.workers.MyWorker.Companion.NotificationsInfo.getBuilder
import java.util.concurrent.TimeUnit

class MyWorker(context: Context, params: WorkerParameters) : BaseWorker(context, params) {
    override suspend fun doWork(): Result {
        val hour = inputData.getInt(HOUR_KEY, ZERO.toInt()).let { inputHour ->
            if (inputHour in correctHoursRange) inputHour else ZERO.toInt()
        }
        val minute = inputData.getInt(MINUTE_KEY, ZERO.toInt()).let { inputMinute ->
            if (inputMinute in correctMinutesRange) inputMinute else ZERO.toInt()
        }

        val times = inputData.getLong(TIMES_KEY, ZERO)

        if (SDK_INT >= O) createChannel()
        val notification = getBuilder(context = applicationContext, times = times)
            .build()
        notificationManager.notify("MyWorker", NOTIFICATION_ID, notification)
        Log.d(
            "Info",
            applicationContext.getString(R.string.notification_has_updated_times, times)
        )

        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<MyWorker>()
                .setInitialDelay(getDelayInSeconds(hour, minute), TimeUnit.SECONDS)
                .setInputData(
                    Data.Builder()
                        .putInt(HOUR_KEY, hour)
                        .putInt(MINUTE_KEY, minute)
                        .putLong(TIMES_KEY, times.inc())
                        .build()
                )
                .addTag(TAG)
                .build()
        )
        return Result.success()
    }

    private val @receiver:StringRes Int.string
        get() = applicationContext.getString(this)

    @RequiresApi(O)
    private fun createChannel() {
        val channel = NotificationChannel(ChannelIdRes.string, ChannelNameRes.string, CHANNEL_IMPORTANCE).apply {
            description = ChannelDescriptionRes.string
        }
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private object NotificationsInfo {
            val ChannelIdRes = R.string.worker_notifications
            val ChannelNameRes = R.string.worker
            val ChannelDescriptionRes = R.string.notification_channel_for_worker_messages
            const val CHANNEL_IMPORTANCE = IMPORTANCE_DEFAULT

            private const val MESSAGE_PRIORITY = PRIORITY_DEFAULT
            private val ChannelDefaultTitleRes = R.string.worker_message
            private val vibratePattern = LongArray(5) { 1_000 }

            fun getBuilder(context: Context, times: Long) =
                NotificationCompat.Builder(context, context.getString(ChannelIdRes))
                    .setSmallIcon(android.R.drawable.ic_popup_sync)
                    .setContentTitle(context.getString(ChannelDefaultTitleRes))
                    .setContentText(
                        context.getString(R.string.notification_has_updated_times, times)
                    )
                    .setPriority(MESSAGE_PRIORITY)
                    .setVibrate(vibratePattern)
        }

        const val TAG = "My worker tag"

        const val HOUR_KEY = "hour"
        const val MINUTE_KEY = "minute"
        private const val TIMES_KEY = "times"

        private const val ZERO = 0L

        private val correctHoursRange = IntRange(0, 23)
        private val correctMinutesRange = IntRange(0, 59)

        const val WORK_NAME = "My custom worker"
    }
}
