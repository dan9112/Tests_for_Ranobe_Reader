package ru.example.proto_datastore

import android.content.Context
import androidx.datastore.dataStore

private val Context.dataStore by dataStore(fileName = "settings.json", serializer = SettingsSerializer)

class DataStoreManager(private val context: Context) {
    suspend fun saveSettings(settingsData: SettingsData) {
        context.dataStore.updateData { data ->
            settingsData.run {
                data.copy(textSize = textSize, backgroundColor = backgroundColor)// settingsData
            }
        }
    }

    suspend fun saveTextSize(textSize: Int) {
        context.dataStore.updateData { data ->
            data.copy(textSize = textSize)
        }
    }

    suspend fun saveBackgroundColor(backgroundColor: ULong) {
        context.dataStore.updateData { data ->
            data.copy(backgroundColor = backgroundColor)
        }
    }

    fun getSettings() = context.dataStore.data
}
