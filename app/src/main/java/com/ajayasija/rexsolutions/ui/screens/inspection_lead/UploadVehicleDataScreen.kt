package com.ajayasija.rexsolutions.ui.screens.inspection_lead

import android.net.Uri
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.ajayasija.rexsolutions.ui.screens.home.InspectionViewModel


enum class GetMedia {
    CHASSIS,
    ODOMETER,
    BALANCED,
    VIDEO
}

@Composable
fun UploadVehicleDataScreen(
    viewModel: InspectionViewModel,
    onNavigateToHomeScreen: () -> Unit,
) {

    var chassisPhoto by remember {
        mutableStateOf<Uri?>(null)
    }
    var odometerPhoto by remember {
        mutableStateOf<Uri?>(null)
    }
    var vehVideo by remember {
        mutableStateOf<Uri?>(null)
    }
    var balancedImages by remember { mutableStateOf(mutableStateListOf<Uri>()) }

    var imgText by remember { mutableStateOf("") }
    var videoText by remember { mutableStateOf("") }

    val state = viewModel.state

    val context = LocalContext.current


    // -----------------------  Condition UI ---------------------
    if (state.isLoading)
        ShowLoading()
    else if (state.acceptedLead == null)
        onNavigateToHomeScreen()
    else if (state.error != null)
        ShowToast(message = state.error, context = context)

    //change img text
    imgText = if (balancedImages.isNotEmpty())
        "${balancedImages.size} image selected"
    else
        "No image selected"

    //chage text after video selection
    videoText = if (vehVideo == null)
        "Click to capture video"
    else
        "1 Video captured"

    //show balanced image dialog
    var showBalancedImageDialog by remember { mutableStateOf(false) }
    if (showBalancedImageDialog) {
        BalancedImageDialog(
            balancedImages,
            showDialog = { showBalancedImageDialog = it },
        )
    }

    // Show toast
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    if (showToast)
        ShowToast(message = toastMessage, context = context)

    //photo picker
    var showPhotoPicker by remember { mutableStateOf(false) }
    var pickMultiple by remember { mutableStateOf(false) }
    var video by remember { mutableStateOf(false) }
    var getMedia by remember { mutableStateOf("") }
    MediaPicker(showPhotoPicker, pickMultiple, video, onImageSelect = {
        when (getMedia) {
            GetMedia.ODOMETER.name -> odometerPhoto = it
            GetMedia.CHASSIS.name -> chassisPhoto = it
            GetMedia.VIDEO.name -> vehVideo = it
        }
        showPhotoPicker = false
    }) {
        showPhotoPicker = false
        balancedImages.addAll(
            if (it.size + balancedImages.size <= 30) it
            else it.take(30 - balancedImages.size)
        )
        /*else{
            showToast = true
            showPhotoPicker = false
        }*/
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
                if (chassisPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Chassis Photo",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.CHASSIS.name
                            }
                    )
                else
                    ImageWithRemoveButton(
                        modifier = Modifier
                            .weight(1f), image = {
                            NetworkImage(
                                model = chassisPhoto,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }) {
                        chassisPhoto = null
                    }
                HorizontalSpace(space = 15.dp)
                if (odometerPhoto == null)
                    ImageUploadPlaceholder(
                        height = 150.dp,
                        text = "Odometer Photo",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                                getMedia = GetMedia.ODOMETER.name
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
            ImageUploadPlaceholder(
                height = 150.dp,
                text = "Click to capture balanced images",
                modifier = Modifier.clickable {
                    if (balancedImages.size < 30) {
                        showPhotoPicker = true
                        pickMultiple = true
                        video = false
                        getMedia = GetMedia.BALANCED.name
                    } /*else {
                        showToast = true
                    }*/
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
            )
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
                        model =  ImageRequest.Builder(LocalContext.current)
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
            RexButton(title = "Confirm & Submit") {
                val images = mutableListOf<Uri>()
                if (chassisPhoto == null && odometerPhoto == null && vehVideo == null && balancedImages.isEmpty()) {
                    showToast = true
                    toastMessage = "Please select image"
                } else {
                    chassisPhoto?.let { it1 -> images.add(it1) }
                    odometerPhoto?.let { it1 -> images.add(it1) }
                    if (balancedImages.isNotEmpty())
                        images.addAll(balancedImages)
                    showToast = false
                    viewModel.onEvent(
                        HomeEvents.SaveToLocal(
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


@Preview
@Composable
fun UploadVehicleDataScreenPreview() {
    //UploadVehicleDataScreen({})
}