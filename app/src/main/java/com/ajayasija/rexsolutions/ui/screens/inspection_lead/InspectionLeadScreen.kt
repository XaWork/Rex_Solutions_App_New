package com.ajayasija.rexsolutions.ui.screens.inspection_lead

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ajayasija.rexsolutions.R
import com.ajayasija.rexsolutions.domain.model.InspectionLeadModel
import com.ajayasija.rexsolutions.ui.components.ContentTopWithUserInfo
import com.ajayasija.rexsolutions.ui.components.CustomFont
import com.ajayasija.rexsolutions.ui.components.RexCard
import com.ajayasija.rexsolutions.ui.components.RexSurface
import com.ajayasija.rexsolutions.ui.components.ShowLoading
import com.ajayasija.rexsolutions.ui.components.ShowToast
import com.ajayasija.rexsolutions.ui.components.VerticalSpace
import com.ajayasija.rexsolutions.ui.screens.home.HomeEvents
import com.ajayasija.rexsolutions.ui.screens.home.InspectionViewModel

@Composable
fun InspectionLeadScreen(
    viewModel: InspectionViewModel,
    onNavigateToUploadVehicleDataScreen: () -> Unit,
    onNavigateToProfileScreen: () -> Unit,
) {

    var innerScale by remember { mutableStateOf(1f) }

    val state = viewModel.state

    if (state.isLoading)
        ShowLoading()
    else if (state.error != null)
        ShowToast(message = "Try again", context = LocalContext.current)
    else if(state.accept) {
        onNavigateToUploadVehicleDataScreen()
        viewModel.onEvent(HomeEvents.ChangeAccept)
    }

   /* LaunchedEffect(key1 = true) {
        viewModel.onEvent(HomeEvents.Inspection)
    }*/

    RexSurface(
        scrollState = rememberScrollState(0),
        contentTop = {
            ContentTopWithUserInfo(title = "Leading")
        }, content = {
            val leadItems = state.lead?.DATA_STATUS
            if (leadItems != null) {
                for (i in leadItems.indices) {
                    SingleInspectionLeadItem(lead = leadItems[i].preinspection, onItemClick = {
                        viewModel.onEvent(HomeEvents.SetAllocation(i, "Y"))

                       // onNavigateToUploadVehicleDataScreen()
                    }, onCancel = {
                        viewModel.onEvent(HomeEvents.SetAllocation(i, "N"))
                    })
                    VerticalSpace(space = 10.dp)
                }
            } else
                ShowLoading()
        })
}

@Composable
fun SingleInspectionLeadItem(
    lead: InspectionLeadModel.DATASTATUS.Preinspection,
    onItemClick: () -> Unit,
    onCancel: () -> Unit
) {
    var context = LocalContext.current
    RexCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable { onItemClick() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val text = lead.run {
                    "$fldvRefNo - $fldvInsurrComName\n$fldvInsuredName\n$fldvVhModel\n$fldvInsuredMobile\n$fldiVhId\n$fldvLocation\n$flddCustApDateTime"
                }
                Column(modifier = Modifier.weight(1f)) {
                    CustomFont(
                        text = text,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        maxLines = 10
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(.2f)
                        .align(Alignment.CenterVertically),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.call),
                        contentDescription = "",
                        modifier = Modifier
                            .size(40.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(5.dp))
                            .padding(10.dp)
                            .align(Alignment.End)
                            .clickable {
                                val phoneUri = Uri.parse("tel:${lead.fldvInsuredMobile}")
                                val intent = Intent(Intent.ACTION_DIAL, phoneUri)
                                context.startActivity(intent)
                            }
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
                            .clickable {
                                val phoneNumberUri =
                                    Uri.parse("https://api.whatsapp.com/send?phone=${lead.fldvInsuredMobile}")
                                val intent = Intent(Intent.ACTION_VIEW, phoneNumberUri)
                                context.startActivity(intent)
                            }
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
                            .clickable { onCancel() }
                    )
                }
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
    //InspectionLeadScreen({}){}

    CustomFont(
        softWrap = true,
        color = Color.White,
        maxLines = 20,
        text = "Hellow how are you ?Hellow how are you ?Hellow how are you ?Hellow how are you ?Hellow how are you ?Hellow how are you ?Hellow how are you \n?Hellow how are you ?Hellow how are you ?Hellow how are you ?Hellow how are you ?Hellow how are you ?Hellow how are you ?Hellow how are you ?Hellow how are you ?Hellow how are you ?"
    )
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