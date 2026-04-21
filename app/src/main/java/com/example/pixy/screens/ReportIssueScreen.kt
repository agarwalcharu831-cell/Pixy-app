package com.example.pixy.screens

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.pixy.components.PixyMapboxPickerMap
import com.example.pixy.model.CATEGORIES
import com.example.pixy.model.IssueMedia
import com.example.pixy.model.MediaSource
import com.example.pixy.model.MediaType
import com.example.pixy.viewmodel.IssueViewModel
import com.google.android.gms.location.LocationServices
import java.io.File

private fun openMediaUri(context: Context, uriString: String, type: MediaType) {
    val mime = if (type == MediaType.IMAGE) "image/*" else "video/*"
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(Uri.parse(uriString), mime)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReportIssueScreen(
    issueViewModel: IssueViewModel,
    onSubmitted: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var pinLat by remember { mutableDoubleStateOf(28.6139) }
    var pinLng by remember { mutableDoubleStateOf(77.2090) }
    var error by remember { mutableStateOf("") }
    var media by remember { mutableStateOf(listOf<IssueMedia>()) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<Location?>(null) }

    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraVideoUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraAction by remember { mutableStateOf<String?>(null) }

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        val fine = granted[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = granted[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fine || coarse) {
            LocationServices.getFusedLocationProviderClient(context)
                .lastLocation
                .addOnSuccessListener { location ->
                    currentLocation = location
                    location?.let {
                        pinLat = it.latitude
                        pinLng = it.longitude
                    }
                }
        }
    }

    val pickImageFromDisk = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            media = media + IssueMedia(it.toString(), MediaType.IMAGE, MediaSource.DISK)
        }
    }

    val pickVideoFromDisk = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            media = media + IssueMedia(it.toString(), MediaType.VIDEO, MediaSource.DISK)
        }
    }

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            media = media + IssueMedia(cameraImageUri.toString(), MediaType.IMAGE, MediaSource.CAMERA)
        }
    }

    val captureVideo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && cameraVideoUri != null) {
            media = media + IssueMedia(cameraVideoUri.toString(), MediaType.VIDEO, MediaSource.CAMERA)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            error = "Camera permission denied"
            pendingCameraAction = null
            return@rememberLauncherForActivityResult
        }

        when (pendingCameraAction) {
            "photo" -> {
                try {
                    val uri = newTempUri(context, "pixy_photo_${System.currentTimeMillis()}.jpg")
                    cameraImageUri = uri
                    takePicture.launch(uri)
                } catch (e: Exception) {
                    error = "Unable to open camera for photo"
                }
            }
            "video" -> {
                try {
                    val uri = newTempUri(context, "pixy_video_${System.currentTimeMillis()}.mp4")
                    cameraVideoUri = uri
                    captureVideo.launch(uri)
                } catch (e: Exception) {
                    error = "Unable to open camera for video"
                }
            }
        }
        pendingCameraAction = null
    }

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            LocationServices.getFusedLocationProviderClient(context)
                .lastLocation
                .addOnSuccessListener { location ->
                    currentLocation = location
                    location?.let {
                        pinLat = it.latitude
                        pinLng = it.longitude
                    }
                }
        } else {
            locationLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    if (showUploadDialog) {
        AlertDialog(
            onDismissRequest = { showUploadDialog = false },
            confirmButton = {},
            title = { Text("Upload Media") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        showUploadDialog = false
                        pickImageFromDisk.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) {
                        Text("Photo from Disk")
                    }

                    Button(onClick = {
                        showUploadDialog = false
                        pickVideoFromDisk.launch("video/*")
                    }) {
                        Text("Video from Disk")
                    }

                    Button(onClick = {
                        showUploadDialog = false
                        pendingCameraAction = "photo"
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }) {
                        Text("Camera Photo")
                    }

                    Button(onClick = {
                        showUploadDialog = false
                        pendingCameraAction = "video"
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }) {
                        Text("Camera Video")
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Issue") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it; error = "" },
                label = { Text("Issue Title *") },
                modifier = Modifier.fillMaxWidth(),
                colors = pixyTextFieldColors()
            )

            Text("Category *")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CATEGORIES.forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat) }
                    )
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                colors = pixyTextFieldColors()
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address / Landmark *") },
                modifier = Modifier.fillMaxWidth(),
                colors = pixyTextFieldColors()
            )

            Button(
                onClick = { showUploadDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Upload")
            }

            if (media.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    media.forEachIndexed { index, item ->
                        Column(
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { openMediaUri(context, item.uri, item.type) }
                                .padding(8.dp)
                        ) {
                            if (item.type == MediaType.IMAGE) {
                                AsyncImage(
                                    model = item.uri,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp)
                                )
                            } else {
                                Text("🎥 Video", modifier = Modifier.size(80.dp))
                            }
                            TextButton(onClick = {
                                media = media.filterIndexed { i, _ -> i != index }
                            }) {
                                Text("Remove")
                            }
                        }
                    }
                }
            }

            Text("Pin Location on Map")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            ) {
                PixyMapboxPickerMap(
                    latitude = pinLat,
                    longitude = pinLng,
                    userLatitude = currentLocation?.latitude,
                    userLongitude = currentLocation?.longitude,
                    onMapTap = { lat, lng ->
                        pinLat = lat
                        pinLng = lng
                    }
                )
            }

            Text("Selected: %.5f, %.5f".format(pinLat, pinLng))

            if (error.isNotBlank()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    when {
                        title.isBlank() -> error = "Please enter a title"
                        category.isBlank() -> error = "Please select a category"
                        address.isBlank() -> error = "Please enter an address"
                        else -> {
                            issueViewModel.submitIssue(
                                title = title,
                                category = category,
                                description = description,
                                latitude = pinLat,
                                longitude = pinLng,
                                address = address,
                                media = media
                            )
                            onSubmitted()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Report")
            }
        }
    }
}

private fun newTempUri(context: Context, name: String): Uri {
    val dir = File(context.cacheDir, "camera").apply { mkdirs() }
    val file = File(dir, name)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}