package com.ajayasija.rexsolutions.ui.screens.inspection_lead

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ajayasija.rexsolutions.R
import com.ajayasija.rexsolutions.ui.components.CustomFont
import com.ajayasija.rexsolutions.ui.components.RexMainTopAppBar
import com.ajayasija.rexsolutions.ui.components.VerticalSpace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionLeadScreen(
    onNavigateToUploadVehicleDataScreen: () -> Unit,
) {

    val leadItems = listOf(
        InspectionLead(),
        InspectionLead(),
        InspectionLead(),
        InspectionLead(),
    )

    Scaffold(topBar = {
        RexMainTopAppBar(navigationIcon = {
        }, actions = {
        })
    }, content = {
        val padding = it
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .background(color = Color.LightGray),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 20.dp)
        ) {
            items(leadItems) { item ->
                SingleInspectionLeadItem(lead = item, { lead ->
                    onNavigateToUploadVehicleDataScreen()
                })
                Divider(thickness = 10.dp)
            }
        }
    })
}

@Composable
fun SingleInspectionLeadItem(lead: InspectionLead, onItemClick: (InspectionLead) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(10.dp)
            .clickable { onItemClick(lead) }
    ) {
        SingleInfoRow("Insp. Comp", lead.company)
        SingleInfoRow("Lead Date", lead.leadDate)
        SingleInfoRow("Vehicle Number", lead.vehicleNumber)
        SingleInfoRow("Vehicle Type", lead.vehicleType)
        SingleInfoRow("Vehicle Model", lead.vehicleModel)
        SingleInfoRow("Client Location", lead.clientLocation)
        SingleInfoRow("Client Name", lead.clientName)
        SingleInfoRow("Client Number", lead.clientNumber)
        VerticalSpace(space = 20.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.accept),
                contentDescription = "",
                modifier = Modifier
                    .weight(1f)
                    .size(30.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.call),
                contentDescription = "",
                modifier = Modifier
                    .weight(1f)
                    .size(30.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.whatsapp),
                contentDescription = "",
                modifier = Modifier
                    .weight(1f)
                    .size(40.dp)
            )
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
    InspectionLeadScreen({})
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