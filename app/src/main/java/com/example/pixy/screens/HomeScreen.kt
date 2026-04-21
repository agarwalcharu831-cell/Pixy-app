package com.example.pixy.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.pixy.components.PixyMapboxHomeMap
import com.example.pixy.model.IssueStatus
import com.example.pixy.viewmodel.IssueViewModel
import com.google.android.gms.location.LocationServices
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    issueViewModel: IssueViewModel,
    onReportClick: () -> Unit,
    onIssueClick: (String) -> Unit,
    onDashboardClick: () -> Unit,
    onViewAllIssues: () -> Unit,
    onMyIssuesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCommunityClick: () -> Unit
) {
    val issues by issueViewModel.issues.collectAsState()
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var selectedVoiceLanguage by remember { mutableStateOf("en-IN") }

    val tts = remember {
        TextToSpeech(context) { }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spokenText = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()
            ?.lowercase(Locale.getDefault())
            .orEmpty()

        when {
            "report" in spokenText && !issueViewModel.currentUserIsAdmin -> onReportClick()
            "my issue" in spokenText && !issueViewModel.currentUserIsAdmin -> onMyIssuesClick()
            "all issue" in spokenText -> onViewAllIssues()
            "dashboard" in spokenText && issueViewModel.currentUserIsAdmin -> onDashboardClick()
            "community" in spokenText -> onCommunityClick()
            "setting" in spokenText -> onSettingsClick()
        }
    }

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        val fine = granted[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = granted[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fine || coarse) {
            val client = LocationServices.getFusedLocationProviderClient(context)
            client.lastLocation.addOnSuccessListener { location ->
                currentLocation = location
            }
        }
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
            val client = LocationServices.getFusedLocationProviderClient(context)
            client.lastLocation.addOnSuccessListener { location ->
                currentLocation = location
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

    if (showVoiceDialog) {
        AlertDialog(
            onDismissRequest = { showVoiceDialog = false },
            title = { Text("Voice Assistant") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Choose language and speak a command.")
                    listOf("en-IN" to "English", "hi-IN" to "Hindi").forEach { (code, label) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedVoiceLanguage = code },
                            tonalElevation = if (selectedVoiceLanguage == code) 4.dp else 0.dp,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    showVoiceDialog = false
                    tts.language = Locale.forLanguageTag(selectedVoiceLanguage)
                    tts.speak(
                        if (selectedVoiceLanguage == "hi-IN") {
                            "कृपया कमांड बोलें"
                        } else {
                            "Please speak your command"
                        },
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "pixy_voice"
                    )
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedVoiceLanguage)
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
                    }
                    try {
                        voiceLauncher.launch(intent)
                    } catch (_: ActivityNotFoundException) {
                    }
                }) {
                    Text("Start")
                }
            },
            dismissButton = {
                TextButton(onClick = { showVoiceDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val openCount = issues.count { it.status == IssueStatus.OPEN }
    val progressCount = issues.count { it.status == IssueStatus.IN_PROGRESS }
    val resolvedCount = issues.count { it.status == IssueStatus.RESOLVED }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Welcome",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatDot("$openCount Open")
                    StatDot("$progressCount Progress")
                    StatDot("$resolvedCount Resolved")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PixyMapboxHomeMap(
                issues = issues,
                userLatitude = currentLocation?.latitude,
                userLongitude = currentLocation?.longitude,
                onIssueClick = onIssueClick
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedVisibility(visible = expanded) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        if (issueViewModel.currentUserIsAdmin) {
                            QuickMenuItem("Dashboard") {
                                expanded = false
                                onDashboardClick()
                            }
                            QuickMenuItem("All Issues") {
                                expanded = false
                                onViewAllIssues()
                            }
                            QuickMenuItem("Community") {
                                expanded = false
                                onCommunityClick()
                            }
                        } else {
                            QuickMenuItem("Report Issue") {
                                expanded = false
                                onReportClick()
                            }
                            QuickMenuItem("My Issues") {
                                expanded = false
                                onMyIssuesClick()
                            }
                            QuickMenuItem("All Issues") {
                                expanded = false
                                onViewAllIssues()
                            }
                            QuickMenuItem("Community") {
                                expanded = false
                                onCommunityClick()
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { showVoiceDialog = true },
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Voice Assistant")
                }

                FloatingActionButton(
                    onClick = { expanded = !expanded },
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun QuickMenuItem(
    title: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 4.dp,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun StatDot(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}