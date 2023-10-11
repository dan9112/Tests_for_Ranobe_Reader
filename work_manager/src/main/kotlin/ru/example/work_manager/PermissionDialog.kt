package ru.example.work_manager

import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
@RequiresApi(TIRAMISU)
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Text(
                text = stringResource(id = if (isPermanentlyDeclined) R.string.grant_permission else R.string.ok),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        if (isPermanentlyDeclined) {
                            onGoToAppSettingsClick()
                        } else {
                            onOkClick()
                        }
                    }
                    .padding(16.dp)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.permission_required),
                textAlign = TextAlign.Center
            )
        },
        text = {
            val context = LocalContext.current

            Text(
                text = permissionTextProvider.getDescription(
                    isPermanentlyDeclined = isPermanentlyDeclined,
                    getString = context::getString
                ),
                textAlign = TextAlign.Center
            )
        },
        modifier = modifier
    )
}

interface PermissionTextProvider {
    fun interface StringResInt {
        operator fun invoke(@StringRes stringResInt: Int): String
    }

    fun getDescription(isPermanentlyDeclined: Boolean, getString: StringResInt): String
}

class PostNotificationsPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean, getString: PermissionTextProvider.StringResInt) =
        getString(
            if (isPermanentlyDeclined) R.string.post_notification_declined_description
            else R.string.post_notification_default_description
        )
}
