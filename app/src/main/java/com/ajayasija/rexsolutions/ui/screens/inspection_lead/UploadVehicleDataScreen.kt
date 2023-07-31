package com.ajayasija.rexsolutions.ui.screens.inspection_lead

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ajayasija.rexsolutions.ui.components.CustomFont
import com.ajayasija.rexsolutions.ui.components.HorizontalSpace
import com.ajayasija.rexsolutions.ui.components.RexButton
import com.ajayasija.rexsolutions.ui.components.RexMainTopAppBar
import com.ajayasija.rexsolutions.ui.components.VerticalSpace
import com.ajayasija.rexsolutions.ui.screens.auth.ImageUploadPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadVehicleDataScreen() {

    var imgText by remember {
        mutableStateOf("No Image found.")
    }
    Scaffold(content = {
        val padding = it
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            VerticalSpace(space = 15.dp)
            CustomFont(
                text = "Vehicle Number",
                fontSize = 18.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold
            )
            VerticalSpace(space = 20.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                ImageUploadPlaceholder(
                    height = 150.dp,
                    text = "Chassis Photo",
                    modifier = Modifier.weight(1f)
                )
                HorizontalSpace(space = 15.dp)
                ImageUploadPlaceholder(
                    height = 150.dp,
                    text = "Odometer Photo",
                    modifier = Modifier.weight(1f)
                )
            }
            VerticalSpace(space = 15.dp)
            ImageUploadPlaceholder(
                height = 150.dp,
                text = "Click to capture balanced images",
            )
            VerticalSpace(space = 15.dp)
            CustomFont(
                text = imgText,
                fontSize = 14.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold
            )
            VerticalSpace(space = 10.dp)
            CustomFont(
                text = "Note: You can upload up to 30 Photos maximum",
                fontSize = 14.sp,
                color = Color.Gray
            )
            VerticalSpace(space = 15.dp)
            ImageUploadPlaceholder(
                height = 150.dp,
                text = "Click to capture video",
            )
            VerticalSpace(space = 15.dp)
            RexButton(title = "Confirm & Submit") {

            }
        }
    })
}

@Preview
@Composable
fun UploadVehicleDataScreenPreview() {
    UploadVehicleDataScreen()
}