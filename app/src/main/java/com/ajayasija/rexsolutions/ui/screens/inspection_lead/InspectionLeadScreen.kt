package com.ajayasija.rexsolutions.ui.screens.inspection_lead

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ajayasija.rexsolutions.R
import com.ajayasija.rexsolutions.data.UserPref
import com.ajayasija.rexsolutions.ui.components.ContentTopWithUserInfo
import com.ajayasija.rexsolutions.ui.components.CustomFont
import com.ajayasija.rexsolutions.ui.components.RexMainTopAppBar
import com.ajayasija.rexsolutions.ui.components.RexSurface
import com.ajayasija.rexsolutions.ui.components.VerticalSpace

@Composable
fun InspectionLeadScreen(
    onNavigateToUploadVehicleDataScreen: () -> Unit,
    onNavigateToProfileScreen: () -> Unit,
) {

    val leadItems = listOf(
        InspectionLead(),
        InspectionLead(),
        InspectionLead(),
        InspectionLead(),
    )

    var innerScale by remember { mutableStateOf(1f) }

    RexSurface(
        scrollState = rememberScrollState(0),
        contentTop = {
            ContentTopWithUserInfo(UserPref(LocalContext.current)){ onNavigateToProfileScreen() }
        }, content = {
            for (item in leadItems) {
                SingleInspectionLeadItem(lead = item) { lead ->
                    onNavigateToUploadVehicleDataScreen()
                }
                Divider(thickness = 10.dp, color = Color.White)
            }
            /*LazyColumn(
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 20.dp),
                userScrollEnabled = false
            ) {
                item {  }
                items(leadItems) { item ->
                    Divider(thickness = 10.dp)
                }
            }*/
        })
}

@Composable
fun SingleInspectionLeadItem(lead: InspectionLead, onItemClick: (InspectionLead) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(10.dp)
            .clickable { onItemClick(lead) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column() {
                val text =
                    "${lead.vehicleModel}\n${lead.clientName} | ${lead.vehicleModel}\n ${lead.clientNumber}\n${lead.vehicleNumber}\n${lead.clientLocation}\n${lead.leadDate}"
                CustomFont(
                    text = text,
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Start
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.call),
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(5.dp))
                        .padding(10.dp)
                )
                VerticalSpace(space = 10.dp)
                Image(
                    painter = painterResource(id = R.drawable.whatsapp),
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.End)
                        .background(color = Color.White, shape = RoundedCornerShape(5.dp))
                        .padding(5.dp)
                )
                VerticalSpace(space = 10.dp)
                Image(
                    painter = painterResource(id = R.drawable.cancel),
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.End)
                        .background(color = Color.White, shape = RoundedCornerShape(5.dp))
                        .padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun SingleInfoRow(title: String, info: String) {
    Row(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = info,
            fontSize = 14.sp,
            color = Color.Black,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun InspectionLeadScreenPreview() {
    InspectionLeadScreen({}){}
}

data class InspectionLead(
    var company: String = "Company Name",
    var leadDate: String = "25-3-2024",
    var vehicleNumber: String = "MH01EJ9876",
    var vehicleModel: String = "STC 800",
    var vehicleType: String = "Four",
    var clientLocation: String = "Pune",
    var clientName: String = "Xa Kaler",
    var clientNumber: String = "8934894398"
)

data class LeadInfo(
    val title: String,
    val info: String
)