@file:OptIn(ExperimentalMaterial3Api::class)

package ru.example.alarm_manager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import ru.example.alarm_manager.ui.theme.RanobeReaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val alarmManager = getSystemService(AlarmManager::class.java)

        setContent {
            RanobeReaderTheme {
                val context = LocalContext.current
                MainScreen(
                    generateAlarm = {
                        setAlarm(alarmManager, context, it)
                    },
                    cancelAlarms = {
                        alarmManager.cancel(
                            PendingIntent.getBroadcast(
                                context,
                                1,
                                Intent(context, AlarmReceiver::class.java),
                                PendingIntent.FLAG_IMMUTABLE
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun MainScreen(generateAlarm: (Long) -> Unit, cancelAlarms: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val currentTimeZone = TimeZone.currentSystemDefault()
        val state = Clock.System.now()
            .toLocalDateTime(currentTimeZone)
            .run {
                rememberTimePickerState(initialHour = hour, initialMinute = minute)
            }

        TimePicker(state = state)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                generateAlarm(
                    Clock.System.now()
                        .let { systemInstant ->
                            systemInstant.toLocalDateTime(currentTimeZone)
                                .run {
                                    LocalDateTime(year, monthNumber, dayOfMonth, state.hour, state.minute)
                                        .toInstant(currentTimeZone)
                                }
                                .let { alarmInstant ->
                                    if (alarmInstant > systemInstant) {
                                        alarmInstant
                                    } else {
                                        alarmInstant.plus(value = 1, unit = DateTimeUnit.DAY, currentTimeZone)
                                    }
                                }
                                .toEpochMilliseconds()
                        }
                )
            }
        ) {
            Text(text = "Create")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = cancelAlarms) {
            Text(text = "Cancel")
        }
    }
}

private fun setAlarm(alarmManager: AlarmManager, context: Context, timeInMillis: Long) {
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
}

@Preview
@Composable
fun MainScreenPreview() = RanobeReaderTheme {
    MainScreen(
        generateAlarm = {},
        cancelAlarms = {}
    )
}
