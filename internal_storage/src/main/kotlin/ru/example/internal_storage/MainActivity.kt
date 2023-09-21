package ru.example.internal_storage

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.example.internal_storage.ui.theme.RanobeReaderTheme
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RanobeReaderTheme {
                var textState by remember { mutableStateOf("") }
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = textState)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                textState = readFile(context)
                            }
                        }
                    ) {
                        Text(text = "Read")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                save(context)
                            }
                        }
                    ) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}

private val textStub
    get() = """
        |...
        |Не выходи из комнаты. О, пускай только комната
        |догадывается, как ты выглядишь. И вообще инкогнито
        |эрго сум, как заметила форме в сердцах субстанция.
        |Не выходи из комнаты! На улице, чай, не Франция.
        |
        |Не будь дураком! Будь тем, чем другие не были.
        |Не выходи из комнаты! То есть дай волю мебели,
        |слейся лицом с обоями. Запрись и забаррикадируйся
        |шкафом от хроноса, космоса, эроса, расы, вируса.
    """.trimMargin()

private suspend fun save(context: Context) {
    withContext(Dispatchers.IO) {
        context.openFileOutput("test.txt", Context.MODE_PRIVATE).use {
            it.write(textStub.toByteArray())
        }
    }
}

private suspend fun readFile(context: Context) = withContext(Dispatchers.IO) {
    try {
        context.openFileInput("test.txt").bufferedReader().useLines { lines ->
            buildString {
                lines.forEach {
                    appendLine(it)
                }
                deleteCharAt(lastIndex)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        ""
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    RanobeReaderTheme {

    }
}
