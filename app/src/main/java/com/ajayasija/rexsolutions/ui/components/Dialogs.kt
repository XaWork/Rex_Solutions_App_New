package com.ajayasija.rexsolutions.ui.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PermissionDialog(
    permissionText: String = "This app needs access to your camera, location and microphone.\n Please allow all to move forward.",
    onDismiss: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = { onDismiss(false) },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider()
                Text(
                    text = "Grant Permission",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                            context.startActivity(intent)
                        }
                        .padding(16.dp)
                )
            }
        },
        title = {
            Text(text = "Permission Required")
        },
        text = {
            Text(text = permissionText)
        },
        modifier = modifier
    )
}