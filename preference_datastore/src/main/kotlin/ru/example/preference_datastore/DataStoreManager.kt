package ru.example.preference_datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import ru.example.preference_datastore.ui.theme.Red

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data_store")

class DataStoreManager(private val context: Context) {
    suspend fun saveSettings(settingsData: SettingsData) {
        context.dataStore.edit { preferences ->
            with(receiver = settingsData) {
                preferences[TEXT_SIZE] = textSize
                preferences[BACKGROUND_COLOR] = backgroundColor
            }
        }
    }

    suspend fun saveTextSize(textSize: Int) {
        context.dataStore.edit { preferences ->
            preferences[TEXT_SIZE] = textSize
        }
    }

    suspend fun saveBackgroundColor(backgroundColor: Long) {
        context.dataStore.edit { preferences ->
            preferences[BACKGROUND_COLOR] = backgroundColor
        }
    }

    fun getSettings() = context.dataStore.data.map { preferences ->
        SettingsData(
            textSize = preferences[TEXT_SIZE] ?: 40,
            backgroundColor = preferences[BACKGROUND_COLOR] ?: Red.value.toLong()
        )
    }

    companion object {
        val TEXT_SIZE = intPreferencesKey("text_size")
        val BACKGROUND_COLOR = longPreferencesKey("background_color")
    }
}
