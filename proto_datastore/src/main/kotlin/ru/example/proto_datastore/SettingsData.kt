package ru.example.proto_datastore

import kotlinx.serialization.Serializable
import ru.example.proto_datastore.ui.theme.Red

@Serializable
data class SettingsData(
    val textSize: Int = 40,
    val backgroundColor: ULong = Red.value
)
