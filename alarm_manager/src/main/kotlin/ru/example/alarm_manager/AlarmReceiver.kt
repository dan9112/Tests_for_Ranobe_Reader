package ru.example.alarm_manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(
            context, "Дзинь-дзинь! Пора кормить кота",
            Toast.LENGTH_LONG
        ).show()
        Log.d("Alarm", "Alarm!")
    }
}
