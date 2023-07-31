package com.ajayasija.rexsolutions.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ajayasija.rexsolutions.R
import com.ajayasija.rexsolutions.ui.components.CustomFont
import com.ajayasija.rexsolutions.ui.components.RexSurface
import com.ajayasija.rexsolutions.ui.components.VerticalSpace

@Composable
fun LoginAsScreen(
    onNavigateToLoginScreen: (String) -> Unit
) {
    RexSurface(content = {
        CustomFont(
            text = "Login As",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        VerticalSpace(space = 50.dp)
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LoginAs(
                title = "Executive",
                icon = R.drawable.executive,
                onClick = { onNavigateToLoginScreen(it) })
            LoginAs(title = "Client", icon = R.drawable.client,
                onClick = { onNavigateToLoginScreen(it) })
        }

    })
}


@Composable
fun LoginAs(title: String, icon: Int, onClick: (loginAs: String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable { onClick(title) }
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "",
            modifier = Modifier.size(100.dp)
        )
        VerticalSpace(space = 10.dp)
        CustomFont(text = title, fontSize = 14.sp, color = Color.Black)
    }
}

@Preview
@Composable
fun LoginAsPreview() {
    LoginAsScreen({})
}