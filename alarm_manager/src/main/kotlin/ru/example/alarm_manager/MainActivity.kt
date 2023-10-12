package ru.example.alarm_manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.example.alarm_manager.ui.theme.RanobeReaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RanobeReaderTheme {
                MainScreen()
            }
        }
    }
}

@Composable
private fun MainScreen() {

}

@Preview
@Composable
fun MainScreenPreview() = RanobeReaderTheme {
    MainScreen()
}
