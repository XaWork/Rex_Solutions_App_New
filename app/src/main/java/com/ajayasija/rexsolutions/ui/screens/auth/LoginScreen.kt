package com.ajayasija.rexsolutions.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ajayasija.rexsolutions.R
import com.ajayasija.rexsolutions.ui.components.CustomFont
import com.ajayasija.rexsolutions.ui.components.RexButton
import com.ajayasija.rexsolutions.ui.components.RexMainTopAppBar
import com.ajayasija.rexsolutions.ui.components.RexTextField
import com.ajayasija.rexsolutions.ui.components.VerticalSpace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegisterScreen: () -> Unit,
    onNavigateToHomeScreen: () -> Unit,
) {
    var username by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            text = {
                Text(text = "Contact your administrator")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("OKAY")
                }
            })
    }

    Scaffold(topBar = {
        RexMainTopAppBar(navigationIcon = {
        }, actions = {})
    }, content = {
        val value = it
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VerticalSpace(space = 30.dp)
            CustomFont(
                text = "Login Employee",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
            VerticalSpace(space = 30.dp)
            RexTextField(
                value = username,
                onValueChange = {
                    username = it
                },
                label = "User Name",
                leadingIcon = R.drawable.baseline_person_24,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            VerticalSpace(space = 30.dp)
            RexTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = "Password",
                leadingIcon = R.drawable.baseline_password_24,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            VerticalSpace(space = 10.dp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                ClickableText(text = buildAnnotatedString {
                    append("RESET/FORGOT PASSWORD")
                    addStyle(
                        style = SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            color = primaryColor
                        ),
                        start = 0,
                        end = length
                    )
                }, onClick = { showDialog = true })
            }
            VerticalSpace(space = 30.dp)
            RexButton(
                title = "Login",
                textAllCaps = true,
                onClick = {onNavigateToHomeScreen()})
            VerticalSpace(space = 30.dp)
            ClickableText(text = buildAnnotatedString {
                append("Register as new user")
                addStyle(
                    style = SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        color = primaryColor
                    ),
                    start = 0,
                    end = length
                )
            }, onClick = {
                onNavigateToRegisterScreen()
            })
        }
    })
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen({}) {}
}
