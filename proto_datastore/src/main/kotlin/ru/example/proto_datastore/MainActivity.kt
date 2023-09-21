package ru.example.proto_datastore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ru.example.proto_datastore.ui.theme.Blue
import ru.example.proto_datastore.ui.theme.Green
import ru.example.proto_datastore.ui.theme.RanobeReaderTheme
import ru.example.proto_datastore.ui.theme.Red

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStoreManager = DataStoreManager(this)

        setContent {
            RanobeReaderTheme {
                val settingsState by dataStoreManager
                    .getSettings()
                    .collectAsState(initial = SettingsData())

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(value = settingsState.backgroundColor)
                ) {
                    MainScreen(dataStoreManager = dataStoreManager, textSize = settingsState.textSize)
                }
            }
        }
    }
}

@Composable
fun MainScreen(dataStoreManager: DataStoreManager, textSize: Int) {
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
                fontSize = textSize.sp
            )
        }
        Row {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    coroutineScope.launch {
                        dataStoreManager.saveBackgroundColor(backgroundColor = Blue.value)
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
                                backgroundColor = Blue.value
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
                        dataStoreManager.saveBackgroundColor(backgroundColor = Green.value)
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
                                backgroundColor = Green.value
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
                        dataStoreManager.saveBackgroundColor(backgroundColor = Red.value)
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
                                backgroundColor = Red.value
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
    val settingsState by dataStoreManager
        .getSettings()
        .collectAsState(initial = SettingsData())

    MainScreen(dataStoreManager = dataStoreManager, textSize = settingsState.textSize)
}
