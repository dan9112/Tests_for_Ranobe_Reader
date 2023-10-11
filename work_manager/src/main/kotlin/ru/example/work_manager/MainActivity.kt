package ru.example.work_manager

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.example.work_manager.ui.theme.RanobeReaderTheme
import ru.example.work_manager.util.getDelayInSeconds
import ru.example.work_manager.workers.MyWorker
import ru.example.work_manager.workers.ResetWorker
import java.util.concurrent.TimeUnit

private val Context.dataStore by preferencesDataStore(name = "permission_datastore")

class MainActivity : ComponentActivity() {

    @RequiresApi(TIRAMISU)
    private val permissionsToRequest = linkedSetOf(
        booleanPreferencesKey(POST_NOTIFICATIONS)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStore = dataStore

        setContent {
            RanobeReaderTheme {
                val workManager = WorkManager.getInstance(LocalContext.current)
                if (SDK_INT >= TIRAMISU) {
                    Content(
                        workManager = workManager,
                        permissionKeys = dataStore
                            .data
                            .map { pref ->
                                Array(permissionsToRequest.size) { pref[permissionsToRequest.elementAt(it)] }
                            }
                            .collectAsState(initial = arrayOfNulls(permissionsToRequest.size))
                            .value,
                        updatePermissionKey = { permissionKey ->
                            runBlocking {
                                dataStore.edit {
                                    it[permissionKey] = true
                                }
                            }
                        }
                    )
                } else {
                    ContentCompat(workManager)
                }
            }
        }
    }

    @Composable
    private fun ContentCompat(workManager: WorkManager) {
        MainScreenAdapter(workManager = workManager)
    }

    @Composable
    @RequiresApi(TIRAMISU)
    private fun Content(
        workManager: WorkManager,
        permissionKeys: Array<Boolean?>,
        updatePermissionKey: (Preferences.Key<Boolean>) -> Unit
    ) {
        val viewModel = viewModel<MainViewModel>()
        val dialogQueue = viewModel.visiblePermissionDialogQueue
        val multiplePermissionsResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { perms ->
                permissionsToRequest.map { it.name }.forEachIndexed { index, permission ->
                    if (shouldShowRequestPermissionRationale(permission)) updatePermissionKey(
                        permissionsToRequest.elementAt(
                            index
                        )
                    )
                    viewModel.onPermissionResult(
                        permission = permission,
                        isGranted = perms[permission] == true
                    )
                }
            }
        )

        MainScreenAdapter(
            workManager = workManager,
            launchPermissionAsk = {
                multiplePermissionsResultLauncher.launch(permissionsToRequest.map { it.name }.toTypedArray())
            }
        )

        dialogQueue
            .reversed()
            .forEach { permission ->
                val permissionKey = permissionKeys[permissionsToRequest.indexOfFirst { it.name == permission }]
                PermissionDialog(
                    permissionTextProvider = when (permission) {
                        POST_NOTIFICATIONS -> {
                            PostNotificationsPermissionTextProvider()
                        }

                        else -> return@forEach
                    },
                    isPermanentlyDeclined = permissionKey != null && !shouldShowRequestPermissionRationale(
                        permission
                    ),
                    onDismiss = viewModel::dismissDialog,
                    onOkClick = {
                        viewModel.dismissDialog()
                        multiplePermissionsResultLauncher.launch(
                            arrayOf(permission)
                        )
                    },
                    onGoToAppSettingsClick = ::openAppSettings
                )
            }
    }

    @Composable
    private fun MainScreenAdapter(workManager: WorkManager, launchPermissionAsk: (() -> Unit)? = null) = MainScreen(
        listWorkInfo = workManager
            .getWorkInfosByTagLiveData(MyWorker.TAG)
            .observeAsState(),
        launchPermissionAsk = launchPermissionAsk,
        launchMyWorker = {

            // todo: вынести наружу!
            val (hour, minute) = arrayOf(6, 30)

            workManager.enqueueUniqueWork(
                MyWorker.WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<MyWorker>()
                    .setInitialDelay(getDelayInSeconds(hour = hour, minute), TimeUnit.SECONDS)
                    .addTag(MyWorker.TAG)
                    .setInputData(
                        Data.Builder()
                            .putInt(MyWorker.HOUR_KEY, hour)
                            .putInt(MyWorker.MINUTE_KEY, minute)
                            .build()
                    )
                    .build()
            )
        },
        launchResetWorker = {
            workManager.run {
                cancelAllWorkByTag(MyWorker.TAG)
                beginUniqueWork(
                    "bla-bla",
                    ExistingWorkPolicy.REPLACE,
                    OneTimeWorkRequest.from(ResetWorker::class.java)
                )
                    .enqueue()
                /*beginWith(OneTimeWorkRequest.from(ResetWorker::class.java))
                    .enqueue()*/
            }
        }
    )
}

fun Activity.openAppSettings() = Intent(
    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
    Uri.fromParts("package", packageName, null)
).let(::startActivity)

@Composable
private fun MainScreen(
    listWorkInfo: State<List<WorkInfo>?>,
    launchPermissionAsk: (() -> Unit)?,
    launchResetWorker: () -> Unit,
    launchMyWorker: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            launchPermissionAsk?.let {
                Button(
                    onClick = launchPermissionAsk
                ) {
                    Text(text = stringResource(R.string.ask_post_notifications_permission))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Text(text = listWorkInfo.value?.lastOrNull()?.state?.toString() ?: "NOT ENQUEUED YET")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = launchMyWorker) {
                Text(text = stringResource(R.string.launch_worker))
            }
            listWorkInfo.value?.lastOrNull()?.state?.let {
                if (!it.isFinished) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = launchResetWorker) {
                        Text(text = stringResource(R.string.cancel_worker))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    RanobeReaderTheme {
        val state = remember { mutableStateOf(null) }
        MainScreen(
            listWorkInfo = state,
            launchPermissionAsk = {},
            launchResetWorker = {},
            launchMyWorker = {}
        )
    }
}
