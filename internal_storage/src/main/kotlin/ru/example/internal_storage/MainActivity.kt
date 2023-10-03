package ru.example.internal_storage

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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

private const val FILE_NAME = "test.txt"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RanobeReaderTheme {
                MainScreen(textState = rememberSaveable { mutableStateOf("") })
            }
        }
    }
}

@Composable
private fun MainScreen(textState: MutableState<String>) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = textState.value)
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    scope.launch {
                        textState.value = readFile(context)
                    }
                }
            ) {
                Text(text = "Read")
            }
            Spacer(modifier = Modifier.width(20.dp))
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

private val Context.textStub
    get() = getString(R.string.brodsky).trimIndent()

private suspend fun save(context: Context) {
    withContext(Dispatchers.IO) {
        context.run {
            openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use {
                it.write(textStub.toByteArray())
            }
        }
    }
}

private suspend fun readFile(context: Context) = withContext(Dispatchers.IO) {
    context.run {
        try {
            openFileInput(FILE_NAME)
                .bufferedReader()
                .useLines { lines ->
                    buildString {
                        lines.forEach { appendLine(it) }
                        deleteCharAt(lastIndex)
                    }
                }
        } catch (e: IOException) {
            e.printStackTrace()
            getString(R.string.read_exception)
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() = RanobeReaderTheme {
    MainScreen(textState = rememberSaveable { mutableStateOf("") })
}
