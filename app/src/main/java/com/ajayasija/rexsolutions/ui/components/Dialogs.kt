package com.ajayasija.rexsolutions.ui.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ajayasija.rexsolutions.R


@Composable
fun ChooseImageOptionsDialog(
    showDialog: (Boolean) -> Unit,
    camera: (Boolean) -> Unit
) {
    Dialog(onDismissRequest = { showDialog(false) }) {
        Surface(shape = RoundedCornerShape(20.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Column(modifier = Modifier.padding(16.dp)) {
                    RexButton(
                        title = "Camera",
                        textAllCaps = true,
                        onClick = {
                            camera(true)
                            showDialog(false)
                        }
                    )
                    VerticalSpace(space = 15.dp)
                    RexButton(
                        title = "Gallery",
                        textAllCaps = true,
                        onClick = {
                            camera(false)
                            showDialog(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChangePasswordDialog(
    showDialog: (Boolean) -> Unit,
    oldPasswordValue: String,
    onOldPasswordChange: (String) -> Unit,
    newPasswordValue: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPasswordValue: String,
    onConfirmPasswordChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Dialog(onDismissRequest = { showDialog(false) }) {
        Surface(shape = RoundedCornerShape(20.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Column(modifier = Modifier.padding(16.dp)) {
                    RexTextField(
                        value = oldPasswordValue,
                        onValueChange = {
                            onOldPasswordChange(it)
                        },
                        label = "Old Password",
                        leadingIcon = R.drawable.baseline_password_24,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    VerticalSpace(space = 15.dp)
                    RexTextField(
                        value = newPasswordValue,
                        onValueChange = {
                            onNewPasswordChange(it)
                        },
                        label = "New Password",
                        leadingIcon = R.drawable.baseline_password_24,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    VerticalSpace(space = 15.dp)
                    RexTextField(
                        value = confirmPasswordValue,
                        onValueChange = {
                            onConfirmPasswordChange(it)
                        },
                        label = "Confirm Password",
                        leadingIcon = R.drawable.baseline_password_24,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    VerticalSpace(space = 15.dp)
                    RexButton(
                        title = "Save",
                        textAllCaps = true,
                        onClick = {
                            onSave()
                            showDialog(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionDialog(
    permissionText: String,
    onDismiss: (Boolean) -> Unit,
    buttonText: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = { onDismiss(false) },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider()
                Text(
                    text = buttonText,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (buttonText == "Grant Permission") {
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null)
                                )
                                context.startActivity(intent)
                            } else {
                                // GPS is not enabled, prompt user to enable it
                                val enableGpsIntent =
                                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                context.startActivity(enableGpsIntent)
                            }
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