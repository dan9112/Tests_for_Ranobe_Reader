package ru.example.preference_datastore

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ru.example.preference_datastore.ui.theme.Blue
import ru.example.preference_datastore.ui.theme.Green
import ru.example.preference_datastore.ui.theme.RanobeReaderTheme
import ru.example.preference_datastore.ui.theme.Red

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStoreManager = DataStoreManager(this)

        setContent {
            RanobeReaderTheme {
                val backgroundColorState = remember { mutableStateOf(Red.value) }
                val textSizeState = remember { mutableStateOf(40) }
                LaunchedEffect(Unit) {
                    dataStoreManager.getSettings().collect { settings ->
                        Log.i("DataStore", "Settings values: [${settings.textSize}, ${settings.backgroundColor}]")
                        backgroundColorState.value = settings.backgroundColor.toULong()
                        textSizeState.value = settings.textSize
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(value = backgroundColorState.value)
                ) {
                    MainScreen(dataStoreManager = dataStoreManager, textSizeState = textSizeState)
                }
            }
        }
    }
}

@Composable
fun MainScreen(dataStoreManager: DataStoreManager, textSizeState: State<Int>) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.5f)
                .wrapContentWidth(align = Alignment.CenterHorizontally)
                .wrapContentHeight(align = Alignment.CenterVertically)
        ) {
            Text(
                text = "Some Text",
                color = Color.White,
                fontSize = textSizeState.value.sp
            )
        }
        Row {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    coroutineScope.launch {
                        dataStoreManager.saveBackgroundColor(backgroundColor = Blue.value.toLong())
                    }
                }
            ) {
                Text(text = "Blue")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    coroutineScope.launch {
                        dataStoreManager.saveSettings(
                            settingsData = SettingsData(
                                textSize = 10,
                                backgroundColor = Blue.value.toLong()
                            )
                        )
                    }
                }
            ) {
                Text(text = "Both")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    coroutineScope.launch {
                        dataStoreManager.saveTextSize(textSize = 10)
                    }
                }
            ) {
                Text(text = "10 dp")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    coroutineScope.launch {
                        dataStoreManager.saveBackgroundColor(backgroundColor = Green.value.toLong())
                    }
                }
            ) {
                Text(text = "Green")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    coroutineScope.launch {
                        dataStoreManager.saveSettings(
                            settingsData = SettingsData(
                                textSize = 30,
                                backgroundColor = Green.value.toLong()
                            )
                        )
                    }
                }
            ) {
                Text(text = "Both")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    coroutineScope.launch {
                        dataStoreManager.saveTextSize(textSize = 30)
                    }
                }
            ) {
                Text(text = "30 dp")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    coroutineScope.launch {
                        dataStoreManager.saveBackgroundColor(backgroundColor = Red.value.toLong())
                    }
                }
            ) {
                Text(text = "Red")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    coroutineScope.launch {
                        dataStoreManager.saveSettings(
                            settingsData = SettingsData(
                                textSize = 50,
                                backgroundColor = Red.value.toLong()
                            )
                        )
                    }
                }
            ) {
                Text(text = "Both")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    coroutineScope.launch {
                        dataStoreManager.saveTextSize(textSize = 50)
                    }
                }
            ) {
                Text(text = "50 dp")
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() = RanobeReaderTheme {
    val dataStoreManager = DataStoreManager(LocalContext.current)
    val textSizeState = remember { mutableStateOf(40) }
    MainScreen(dataStoreManager = dataStoreManager, textSizeState = textSizeState)
}
