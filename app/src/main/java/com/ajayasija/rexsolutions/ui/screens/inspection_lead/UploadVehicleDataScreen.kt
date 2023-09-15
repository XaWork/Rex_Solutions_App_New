package com.ajayasija.rexsolutions.ui.screens.inspection_lead

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.ajayasija.rexsolutions.domain.model.VehicleMedia
import com.ajayasija.rexsolutions.ui.components.CustomFont
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun UploadVehicleDataScreen(
    viewModel: InspectionViewModel,
    onNavigateToHomeScreen: () -> Unit,
) {


    var vehVideo by remember {
        mutableStateOf<String?>(null)
    }
    var selectedIndex by remember {
        mutableStateOf<Int>(0)
    }
    var imageName by remember {
        mutableStateOf<String>("")
    }

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

    //chage text after video selection
    videoText = if (vehVideo == null)
        "Click to capture video"
    else
        "1 Video captured"

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

    if (showPhotoPicker) {
        MediaPicker(
            state.location!!,
            showPhotoPicker,
            pickMultiple,
            video,
            showDialog = showPhotoPicker,
            imageName = imageName,
            //changeDialogValue = { showMediaChooserDialog = it },
            onImageSelect = {
                viewModel.onEvent(
                    HomeEvents.UpdateImageUri(
                        imageUri = it!!,
                        selectedIndex,
                        context
                    )
                )
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
                .fillMaxSize()
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
                Column {
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
            VerticalSpace(space = 15.dp)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                items(state.vehicleMediaList) { media ->
                    Column {
                        VehicleMediaView(media = media, takeMedia = {
                            if (checkLocation(context, viewModel)) {
                                imageName = createImageName(
                                    state.acceptedLead!!.fldiVhId,
                                    media.index.toString()
                                )
                                selectedIndex = media.index
                                showPhotoPicker = true
                                pickMultiple = false
                                video = false
                            }
                        }, deleteMedia = {
                            viewModel.onEvent(
                                HomeEvents.DeleteImageFromAws(
                                    media.index,
                                    context
                                )
                            )
                        })
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (media.error) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "refresh",
                                    tint = Color.Red,
                                    modifier = Modifier.clickable {
                                        viewModel.onEvent(
                                            HomeEvents.UpdateImageUri(
                                                imageUri = media.uri!!,
                                                index = media.index,
                                                context = context
                                            )
                                        )
                                    }
                                )

                            } else {
                                LinearProgressIndicator(
                                    progress = media.progress.toFloat(),
                                    modifier = Modifier
                                        .height(20.dp)
                                )
                                CustomFont("${media.progress}%", color = Color.White)
                            }
                        }
                    }
                }
            }

            RexButton(title = "Confirm & Submit") {
                var allImageSet = true
                for (media in state.vehicleMediaList) {
                    if (media.uri == null) {
                        allImageSet = false
                        break
                    }
                }
                if (!allImageSet) {
                    Toast.makeText(context, "Please upload all images", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context,
                        "Wait a minute, Don't press any button.",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.onEvent(
                        HomeEvents.SaveToLocal(
                            location = state.location!!,
                            context = context,
                        )
                    )
                }
            }
        }

    })
}

@Composable
fun VehicleMediaView(
    media: VehicleMedia,
    takeMedia: () -> Unit,
    deleteMedia: () -> Unit
) {
    if (media.uri == null)
        ImageUploadPlaceholder(
            height = 150.dp,
            text = media.title,
            modifier = Modifier
                .padding(10.dp)
                .clickable {
                    takeMedia()
                }
        )
    else
        ImageWithRemoveButton(
            image = {
                NetworkImage(
                    model = media.uri,
                    modifier = Modifier
                        .padding(10.dp)
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
            }) {
            deleteMedia()
        }
}

fun createImageName(vehId: String, number: String): String {
    val timeStampFormat = SimpleDateFormat("ddMMyyyy", Locale.ENGLISH)
    val myDate = Date()
    val strDate = timeStampFormat.format(myDate)
    return "${vehId.uppercase().trim()}_${strDate.trim()}_${number.trim()}"
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

fun checkLocation(context: Context, viewModel: InspectionViewModel): Boolean {
    val state = viewModel.state
    return if (state.location != null)
        true
    else {
        viewModel.onEvent(HomeEvents.GetLocation(context))
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