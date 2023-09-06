package com.ajayasija.rexsolutions.ui.screens.inspection_lead

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.request.ImageRequest
import com.ajayasija.rexsolutions.ui.components.CustomFont
import com.ajayasija.rexsolutions.ui.components.HorizontalSpace
import com.ajayasija.rexsolutions.ui.components.ImageUploadPlaceholder
import com.ajayasija.rexsolutions.ui.components.ImageWithRemoveButton
import com.ajayasija.rexsolutions.ui.components.MediaPicker
import com.ajayasija.rexsolutions.ui.components.NetworkImage
import com.ajayasija.rexsolutions.ui.components.RexButton
import com.ajayasija.rexsolutions.ui.components.ShowLoading
import com.ajayasija.rexsolutions.ui.components.ShowToast
import com.ajayasija.rexsolutions.ui.components.VerticalSpace
import com.ajayasija.rexsolutions.ui.screens.home.HomeEvents
import com.ajayasija.rexsolutions.ui.screens.home.HomeState
import com.ajayasija.rexsolutions.ui.screens.home.InspectionViewModel
import java.io.File


enum class GetMedia {
    CHASSIS,
    ODOMETER,
    REGISTRATIONBOOK,
    ENGINENUMBER,
    FRONTWSGLASS,
    FULLFRONT,
    FRONTUNDERCARRIAGE,
    BONNETOPENFORENGINE,
    ROOFFROMOUTSIDE,
    FRONTVLEFT,
    FULLLEFT,
    BACKVLEFT,
    BACKFULL,
    BACKUNDERCARRIAGE,
    BACKDICKEYOPEN,
    BACKVRIGHT,
    FULLRIGHT,
    FRONTVRIGHT,
    SELIEWITHCAR,
    TYRE1,
    TYRE2,
    TYRE3,
    TYRE4,
    DASHBOARDINSIDE,
    FRONTSEAT,
    BACKSEAT,
    ROOFFROMINSIDE,
    EXTRA1,
    EXTRA2,
    EXTRA3,
    BALANCED,
    VIDEO
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun UploadVehicleDataScreen(
    viewModel: InspectionViewModel,
    onNavigateToHomeScreen: () -> Unit,
) {

    var registrationBookPhoto by remember { mutableStateOf<Uri?>(null) }
    var odometerPhoto by remember { mutableStateOf<Uri?>(null) }
    var chassisNumberPhoto by remember { mutableStateOf<Uri?>(null) }
    var engineNumberPhoto by remember { mutableStateOf<Uri?>(null) }
    var frontWsGlassPhoto by remember { mutableStateOf<Uri?>(null) }
    var fullFrontPhoto by remember { mutableStateOf<Uri?>(null) }
    var frontUnderCarriagePhoto by remember { mutableStateOf<Uri?>(null) }
    var bonnetOpenForEnginePhoto by remember { mutableStateOf<Uri?>(null) }
    var roofFromOutsidePhoto by remember { mutableStateOf<Uri?>(null) }
    var frontVLeftPhoto by remember { mutableStateOf<Uri?>(null) }
    var fullLeftPhoto by remember { mutableStateOf<Uri?>(null) }
    var backVLeftPhoto by remember { mutableStateOf<Uri?>(null) }
    var backFullPhoto by remember { mutableStateOf<Uri?>(null) }
    var backUnderCarriagePhoto by remember { mutableStateOf<Uri?>(null) }
    var backDickeyOpenPhoto by remember { mutableStateOf<Uri?>(null) }
    var backVRightPhoto by remember { mutableStateOf<Uri?>(null) }
    var fullRightPhoto by remember { mutableStateOf<Uri?>(null) }
    var frontVRightPhoto by remember { mutableStateOf<Uri?>(null) }
    var selfieWithCarPhoto by remember { mutableStateOf<Uri?>(null) }
    var tyre1Photo by remember { mutableStateOf<Uri?>(null) }
    var tyre2Photo by remember { mutableStateOf<Uri?>(null) }
    var tyre3Photo by remember { mutableStateOf<Uri?>(null) }
    var tyre4Photo by remember { mutableStateOf<Uri?>(null) }
    var dashboardInsidePhoto by remember { mutableStateOf<Uri?>(null) }
    var frontSeatPhoto by remember { mutableStateOf<Uri?>(null) }
    var backSeatPhoto by remember { mutableStateOf<Uri?>(null) }
    var roofFromInsidePhoto by remember { mutableStateOf<Uri?>(null) }
    var extra1Photo by remember { mutableStateOf<Uri?>(null) }
    var extra2Photo by remember { mutableStateOf<Uri?>(null) }
    var extra3Photo by remember { mutableStateOf<Uri?>(null) }

    var vehVideo by remember {
        mutableStateOf<String?>(null)
    }
    //val balancedImages by remember { mutableStateOf(mutableStateListOf<Uri>()) }

    var imgText by remember { mutableStateOf("") }
    var videoText by remember { mutableStateOf("") }

    val state = viewModel.state
    val context = LocalContext.current

    //get location
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(HomeEvents.GetLocation(context))
    }


    // -----------------------  Condition UI ---------------------
    if (state.isLoading)
        ShowLoading()
    else if (state.acceptedLead == null && state.allocationStatus != null)
        onNavigateToHomeScreen()
    else if (state.error != null)
        ShowToast(message = state.error, context = context)
    else if (state.uploadVideo != null) {
        ShowToast(message = "Video upload successfully", context = context)
        vehVideo = null
        viewModel.state = viewModel.state.copy(uploadVideo = null)
    }

    /* //change img text
     imgText = if (balancedImages.isNotEmpty())
         "${balancedImages.size} image selected"
     else
         "No image selected"*/

    //chage text after video selection
    videoText = if (vehVideo == null)
        "Click to capture video"
    else
        "1 Video captured"

    //show balanced image dialog
    /*var showBalancedImageDialog by remember { mutableStateOf(false) }
    if (showBalancedImageDialog) {
        BalancedImageDialog(
            balancedImages,
            showDialog = { showBalancedImageDialog = it },
        )
    }*/

    // Show toast
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    if (showToast)
        ShowToast(message = toastMessage, context = context)

    //photo picker
    var showPhotoPicker by remember { mutableStateOf(false) }
    var pickMultiple by remember { mutableStateOf(false) }
    var showMediaChooserDialog by remember { mutableStateOf(false) }
    var video by remember { mutableStateOf(false) }
    var getMedia by remember { mutableStateOf("") }

    if(showPhotoPicker){
        MediaPicker(
            state.location!!,
            showPhotoPicker,
            pickMultiple,
            video,
            showDialog = showPhotoPicker,
            //changeDialogValue = { showMediaChooserDialog = it },
            onImageSelect = {
                when (getMedia) {
                    GetMedia.REGISTRATIONBOOK.name -> registrationBookPhoto = it
                    GetMedia.ODOMETER.name -> odometerPhoto = it
                    GetMedia.CHASSIS.name -> chassisNumberPhoto = it
                    GetMedia.ENGINENUMBER.name -> engineNumberPhoto = it
                    GetMedia.FRONTWSGLASS.name -> frontWsGlassPhoto = it
                    GetMedia.FULLFRONT.name -> fullFrontPhoto = it
                    GetMedia.FRONTUNDERCARRIAGE.name -> frontUnderCarriagePhoto = it
                    GetMedia.BONNETOPENFORENGINE.name -> bonnetOpenForEnginePhoto = it
                    GetMedia.ROOFFROMOUTSIDE.name -> roofFromOutsidePhoto = it
                    GetMedia.FRONTVLEFT.name -> frontVLeftPhoto = it
                    GetMedia.FULLLEFT.name -> fullLeftPhoto = it
                    GetMedia.BACKVLEFT.name -> backVLeftPhoto = it
                    GetMedia.BACKFULL.name -> backFullPhoto = it
                    GetMedia.BACKUNDERCARRIAGE.name -> backUnderCarriagePhoto = it
                    GetMedia.BACKDICKEYOPEN.name -> backDickeyOpenPhoto = it
                    GetMedia.BACKVRIGHT.name -> backVRightPhoto = it
                    GetMedia.FULLRIGHT.name -> fullRightPhoto = it
                    GetMedia.FRONTVRIGHT.name -> frontVRightPhoto = it
                    GetMedia.SELIEWITHCAR.name -> selfieWithCarPhoto = it
                    GetMedia.TYRE1.name -> tyre1Photo = it
                    GetMedia.TYRE2.name -> tyre2Photo = it
                    GetMedia.TYRE3.name -> tyre3Photo = it
                    GetMedia.TYRE4.name -> tyre4Photo = it
                    GetMedia.DASHBOARDINSIDE.name -> dashboardInsidePhoto = it
                    GetMedia.FRONTSEAT.name -> frontSeatPhoto = it
                    GetMedia.BACKSEAT.name -> backSeatPhoto = it
                    GetMedia.ROOFFROMINSIDE.name -> roofFromInsidePhoto = it
                    GetMedia.EXTRA1.name -> extra1Photo = it
                    GetMedia.EXTRA2.name -> extra2Photo = it
                    GetMedia.EXTRA3.name -> extra3Photo = it
                }
                showPhotoPicker = false
            },
            onDismissMediaPicker = {
                showPhotoPicker = false
            },
            onVideoSelect = {
                vehVideo = it
                showPhotoPicker = false
            }) {
            showPhotoPicker = false
        }
    }



    // -----------------------  UI ---------------------

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
            state.acceptedLead?.fldiVhId?.let { vehId ->
                CustomFont(
                    text = vehId,
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold
                )
            }
            VerticalSpace(space = 20.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (registrationBookPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Registration Book ",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (checkLocation(context, state)) {
                                    showPhotoPicker = true
                                    pickMultiple = false
                                    video = false
                                    getMedia = GetMedia.REGISTRATIONBOOK.name
                                }
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = registrationBookPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        registrationBookPhoto = null
                    }
                HorizontalSpace(space = 15.dp)
                if (odometerPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Odometer",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (checkLocation(context, state)) {
                                    showPhotoPicker = true
                                    pickMultiple = false
                                    video = false
                                    getMedia = GetMedia.ODOMETER.name
                                }
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = odometerPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        odometerPhoto = null
                    }
            }
            VerticalSpace(space = 15.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (chassisNumberPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Chassis Number ",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (checkLocation(context, state)) {
                                    showPhotoPicker = true
                                    pickMultiple = false
                                    video = false
                                    getMedia = GetMedia.CHASSIS.name
                                }
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = chassisNumberPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        chassisNumberPhoto = null
                    }
                HorizontalSpace(space = 15.dp)
                if (engineNumberPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Engine Number",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (checkLocation(context, state)) {
                                    showPhotoPicker = true
                                    pickMultiple = false
                                    video = false
                                    getMedia = GetMedia.ENGINENUMBER.name
                                }
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = engineNumberPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        engineNumberPhoto = null
                    }
            }
            VerticalSpace(space = 15.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (frontWsGlassPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Front WS Glass",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (checkLocation(context, state)) {
                                    showPhotoPicker = true
                                    pickMultiple = false
                                    video = false
                                    getMedia = GetMedia.FRONTWSGLASS.name
                                }
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = frontWsGlassPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        frontWsGlassPhoto = null
                    }
                HorizontalSpace(space = 15.dp)
                if (fullFrontPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Full Front",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (checkLocation(context, state)) {
                                    showPhotoPicker = true
                                    pickMultiple = false
                                    video = false
                                    getMedia = GetMedia.FULLFRONT.name
                                }
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = fullFrontPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        fullFrontPhoto = null
                    }
            }
            VerticalSpace(space = 15.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (frontUnderCarriagePhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Front Under Carriage",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (checkLocation(context, state)) {
                                    showPhotoPicker = true
                                    pickMultiple = false
                                    video = false
                                    getMedia = GetMedia.FRONTUNDERCARRIAGE.name
                                }
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = frontUnderCarriagePhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        frontUnderCarriagePhoto = null
                    }
                HorizontalSpace(space = 15.dp)
                if (bonnetOpenForEnginePhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Bonnet Open For Engine",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (checkLocation(context, state)) {
                                    showPhotoPicker = true
                                    pickMultiple = false
                                    video = false
                                    getMedia = GetMedia.BONNETOPENFORENGINE.name
                                }
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = bonnetOpenForEnginePhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        bonnetOpenForEnginePhoto = null
                    }
            }
            VerticalSpace(space = 15.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (roofFromOutsidePhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Roof From Outside",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (checkLocation(context, state)) {
                                    showPhotoPicker = true
                                    pickMultiple = false
                                    video = false
                                    getMedia = GetMedia.ROOFFROMOUTSIDE.name
                                }
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = roofFromOutsidePhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        roofFromOutsidePhoto = null
                    }
                HorizontalSpace(space = 15.dp)
                if (frontVLeftPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Front V Left",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (checkLocation(context, state)) {
                                    showPhotoPicker = true
                                    pickMultiple = false
                                    video = false
                                    getMedia = GetMedia.FRONTVLEFT.name
                                }
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = frontVLeftPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        frontVLeftPhoto = null
                    }
            }
            VerticalSpace(space = 15.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (fullLeftPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Full Left",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (checkLocation(context, state)) {
                                    showPhotoPicker = true
                                    pickMultiple = false
                                    video = false
                                    getMedia = GetMedia.FULLLEFT.name
                                }
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = fullLeftPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        fullLeftPhoto = null
                    }
                HorizontalSpace(space = 15.dp)
                if (backVLeftPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Back V Left",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (checkLocation(context, state)) {
                                    showPhotoPicker = true
                                    pickMultiple = false
                                    video = false
                                    getMedia = GetMedia.BACKVLEFT.name
                                }
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = backVLeftPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        backVLeftPhoto = null
                    }
            }
            VerticalSpace(space = 15.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (backFullPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Back Full",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (checkLocation(context, state)) {
                                    showPhotoPicker = true
                                    pickMultiple = false
                                    video = false
                                    getMedia = GetMedia.BACKFULL.name
                                }
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = backFullPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        backFullPhoto = null
                    }
                HorizontalSpace(space = 15.dp)
                if (backUnderCarriagePhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Back Under Carriage",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (checkLocation(context, state)) {
                                    showPhotoPicker = true
                                    pickMultiple = false
                                    video = false
                                    getMedia = GetMedia.BACKUNDERCARRIAGE.name
                                }
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = backUnderCarriagePhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        backUnderCarriagePhoto = null
                    }
            }
            VerticalSpace(space = 15.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (backDickeyOpenPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Back Dickey Open",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.BACKDICKEYOPEN.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = backDickeyOpenPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        backDickeyOpenPhoto = null
                    }
                HorizontalSpace(space = 15.dp)
                if (backVRightPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Back V Right",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.BACKVRIGHT.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = backVRightPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        backVRightPhoto = null
                    }
            }
            VerticalSpace(space = 15.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (fullRightPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Full Right",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.FULLRIGHT.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = fullRightPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        fullRightPhoto = null
                    }
                HorizontalSpace(space = 15.dp)
                if (frontVRightPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Front V Right",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.FRONTVRIGHT.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = frontVRightPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        frontVRightPhoto = null
                    }
            }
            VerticalSpace(space = 15.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (selfieWithCarPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Selfie With Car",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.SELIEWITHCAR.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = selfieWithCarPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        selfieWithCarPhoto = null
                    }
                HorizontalSpace(space = 15.dp)
                if (tyre1Photo == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Tyre 1",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.TYRE1.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = tyre1Photo,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        tyre1Photo = null
                    }
            }
            VerticalSpace(space = 15.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (tyre2Photo == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Tyre 2",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.TYRE2.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = tyre2Photo,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        tyre2Photo = null
                    }
                HorizontalSpace(space = 15.dp)
                if (tyre3Photo == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Tyre 3",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.TYRE3.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = tyre3Photo,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        tyre3Photo = null
                    }
            }
            VerticalSpace(space = 15.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (tyre4Photo == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Tyre 4",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.TYRE4.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = tyre4Photo,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        tyre4Photo = null
                    }
                HorizontalSpace(space = 15.dp)
                if (dashboardInsidePhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Dashboard Inside",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.DASHBOARDINSIDE.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = dashboardInsidePhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        dashboardInsidePhoto = null
                    }
            }
            VerticalSpace(space = 15.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (frontSeatPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Front Seat ",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.FRONTSEAT.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = frontSeatPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        frontSeatPhoto = null
                    }
                HorizontalSpace(space = 15.dp)
                if (backSeatPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Back Seat",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.BACKSEAT.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = backSeatPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        backSeatPhoto = null
                    }
            }
            VerticalSpace(space = 15.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (roofFromInsidePhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Roof From Inside",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.ROOFFROMINSIDE.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = roofFromInsidePhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        roofFromInsidePhoto = null
                    }
                HorizontalSpace(space = 15.dp)
                if (extra1Photo == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Extra 1",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.EXTRA1.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = extra1Photo,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        extra1Photo = null
                    }
            }
            VerticalSpace(space = 15.dp)
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (extra2Photo == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Extra 2",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.EXTRA2.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = extra2Photo,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        extra2Photo = null
                    }
                HorizontalSpace(space = 15.dp)
                if (extra3Photo == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Extra 3",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.EXTRA3.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = extra3Photo,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        extra3Photo = null
                    }
            }
            /*     VerticalSpace(space = 15.dp)
                 ImageUploadPlaceholder(
                     height = 150.dp,
                     text = "Click to capture balanced images",
                     modifier = Modifier.clickable {
                         if (balancedImages.size < 30) {
                             showPhotoPicker = true
                             pickMultiple = true
                             video = false
                             getMedia = GetMedia.BALANCED.name
                         } *//*else {
                        showToast = true
                    }*//*
                })

            VerticalSpace(space = 15.dp)
            CustomFont(
                text = imgText,
                fontSize = 14.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    if (balancedImages.isNotEmpty()) {
                        showBalancedImageDialog = true
                    }
                }
            )
            VerticalSpace(space = 10.dp)
            CustomFont(
                text = "Note: You can upload up to 30 Photos maximum",
                fontSize = 14.sp,
                color = if (balancedImages.size < 30) Color.Gray else Color.Red
            )*/
            VerticalSpace(space = 15.dp)
            if (vehVideo == null)
                ImageUploadPlaceholder(
                    height = 150.dp,
                    text = videoText,
                    modifier = Modifier.clickable {
                        showPhotoPicker = true
                        pickMultiple = false
                        video = true
                        getMedia = GetMedia.VIDEO.name
                    }
                )
            else
                ImageWithRemoveButton(image = {
                    NetworkImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://img.freepik.com/free-vector/flat-clapperboard-icon_1063-38.jpg")
                            .crossfade(true)
                            .build(),
                        modifier = Modifier
                            .height(150.dp),
                        contentScale = ContentScale.Crop
                    )
                }) {
                    vehVideo = null
                }
            VerticalSpace(space = 15.dp)
            if (vehVideo != null) {
                Log.e("video", "veh video $vehVideo")
                RexButton(title = "Upload Video") {
                    if (vehVideo != null)
                        viewModel.onEvent(HomeEvents.UploadVideo(File(vehVideo!!)))
                    else {
                        toastMessage = "Please select video"
                        showToast = true
                    }
                }
            }
            RexButton(title = "Confirm & Submit") {
                val images = mutableListOf<Uri>()
                if (chassisNumberPhoto == null ||
                    odometerPhoto == null ||
                    registrationBookPhoto == null ||
                    engineNumberPhoto == null ||
                    frontWsGlassPhoto == null ||
                    fullFrontPhoto == null ||
                    frontUnderCarriagePhoto == null ||
                    bonnetOpenForEnginePhoto == null ||
                    roofFromOutsidePhoto == null ||
                    frontVLeftPhoto == null ||
                    fullLeftPhoto == null ||
                    backVLeftPhoto == null ||
                    backFullPhoto == null ||
                    backUnderCarriagePhoto == null ||
                    backDickeyOpenPhoto == null ||
                    backVRightPhoto == null ||
                    fullRightPhoto == null ||
                    frontVRightPhoto == null ||
                    selfieWithCarPhoto == null ||
                    tyre1Photo == null ||
                    tyre2Photo == null ||
                    tyre3Photo == null ||
                    tyre4Photo == null ||
                    dashboardInsidePhoto == null ||
                    frontSeatPhoto == null ||
                    backSeatPhoto == null ||
                    roofFromInsidePhoto == null ||
                    extra1Photo == null ||
                    extra2Photo == null ||
                    extra3Photo == null
                //vehVideo == null
                ) {
                    showToast = true
                    toastMessage = "Please select image"
                } else {
                    Toast.makeText(
                        context,
                        "Wait a minute, Don't press any button.",
                        Toast.LENGTH_LONG
                    ).show()
                    chassisNumberPhoto?.let { it1 -> images.add(it1) }
                    odometerPhoto?.let { it1 -> images.add(it1) }
                    registrationBookPhoto?.let { it1 -> images.add(it1) }
                    engineNumberPhoto?.let { it1 -> images.add(it1) }
                    frontWsGlassPhoto?.let { it1 -> images.add(it1) }
                    fullFrontPhoto?.let { it1 -> images.add(it1) }
                    frontUnderCarriagePhoto?.let { it1 -> images.add(it1) }
                    bonnetOpenForEnginePhoto?.let { it1 -> images.add(it1) }
                    roofFromOutsidePhoto?.let { it1 -> images.add(it1) }
                    frontVLeftPhoto?.let { it1 -> images.add(it1) }
                    fullLeftPhoto?.let { it1 -> images.add(it1) }
                    backVLeftPhoto?.let { it1 -> images.add(it1) }
                    backFullPhoto?.let { it1 -> images.add(it1) }
                    backUnderCarriagePhoto?.let { it1 -> images.add(it1) }
                    backDickeyOpenPhoto?.let { it1 -> images.add(it1) }
                    backVRightPhoto?.let { it1 -> images.add(it1) }
                    fullRightPhoto?.let { it1 -> images.add(it1) }
                    frontVRightPhoto?.let { it1 -> images.add(it1) }
                    selfieWithCarPhoto?.let { it1 -> images.add(it1) }
                    tyre1Photo?.let { it1 -> images.add(it1) }
                    tyre2Photo?.let { it1 -> images.add(it1) }
                    tyre3Photo?.let { it1 -> images.add(it1) }
                    tyre4Photo?.let { it1 -> images.add(it1) }
                    dashboardInsidePhoto?.let { it1 -> images.add(it1) }
                    frontSeatPhoto?.let { it1 -> images.add(it1) }
                    backSeatPhoto?.let { it1 -> images.add(it1) }
                    roofFromInsidePhoto?.let { it1 -> images.add(it1) }
                    extra1Photo?.let { it1 -> images.add(it1) }
                    extra2Photo?.let { it1 -> images.add(it1) }
                    extra3Photo?.let { it1 -> images.add(it1) }

                    showToast = false
                    viewModel.onEvent(
                        HomeEvents.SaveToLocal(
                            location =state.location!!,
                            images,
                            context = context,
                            // video = if (vehVideo != null) vehVideo else null
                        )
                    )
                }
            }
        }
    })
}


@Composable
fun BalancedImageDialog(
    images: MutableList<Uri>,
    showDialog: (Boolean) -> Unit,
) {

    Dialog(onDismissRequest = { showDialog(false) }) {
        Surface(shape = RoundedCornerShape(20.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(10.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(images) { image ->
                    ImageWithRemoveButton(size = 10.dp, image = {
                        NetworkImage(
                            model = image,
                            modifier = Modifier
                                .height(70.dp)
                                .padding(5.dp),
                            contentScale = ContentScale.Crop
                        )
                    }) {
                        images.remove(image)
                        if (images.isEmpty())
                            showDialog(false)
                    }
                }
            }
        }
    }
}

fun checkLocation(context: Context, state: HomeState): Boolean {
    return if (state.location != null)
        true
    else {
        Toast.makeText(
            context,
            "Please check location permission is granted and GPS is enabled.",
            Toast.LENGTH_LONG
        ).show()
        false
    }
}


@Preview
@Composable
fun UploadVehicleDataScreenPreview() {
    //UploadVehicleDataScreen({})
}