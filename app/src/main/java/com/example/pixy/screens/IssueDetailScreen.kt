package com.example.pixy.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.pixy.components.PixyMapboxDetailMap
import com.example.pixy.components.StatusChip
import com.example.pixy.model.IssueStatus
import com.example.pixy.model.MediaType
import com.example.pixy.viewmodel.IssueViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatTime(time: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(time))
}

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun IssueDetailScreen(
    issueId: String,
    issueViewModel: IssueViewModel,
    onBack: () -> Unit,
    onReportFake: () -> Unit
) {
    val issues by issueViewModel.issues.collectAsState()
    val issue = issues.find { it.id == issueId }
    var comment by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (issue == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Issue Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    if (!issueViewModel.currentUserIsAdmin) {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Report fake issue") },
                                onClick = {
                                    menuExpanded = false
                                    onReportFake()
                                }
                            )
                        }
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
            AnimatedVisibility(visible = true) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(issue.title, style = MaterialTheme.typography.headlineSmall)
                    StatusChip(issue.status)
                    Text(issue.category)
                    Text(issue.description.ifBlank { "No description provided." })
                    Text("📍 ${issue.address}")
                    Text("Votes: ${issue.votes}")
                    Text("Reported at: ${formatTime(issue.createdAt)}")
                    Text("Submitted by: ${issue.reportedByName.ifBlank { "Unknown User" }}")
                    Text("Email: ${issue.reportedByEmail.ifBlank { "N/A" }}")
                }
            }

            if (issue.media.isNotEmpty()) {
                Text("Media", style = MaterialTheme.typography.titleMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    issue.media.forEach { item ->
                        if (item.type == MediaType.IMAGE) {
                            AsyncImage(
                                model = item.uri,
                                contentDescription = null,
                                modifier = Modifier
                                    .height(120.dp)
                                    .fillMaxWidth(0.45f)
                                    .clickable { openMediaUri(context, item.uri, item.type) }
                            )
                        } else {
                            AndroidView(
                                factory = { ctx ->
                                    VideoView(ctx).apply {
                                        setVideoURI(item.uri.toUri())
                                        seekTo(200)
                                    }
                                },
                                modifier = Modifier
                                    .height(120.dp)
                                    .fillMaxWidth(0.45f)
                                    .clickable { openMediaUri(context, item.uri, item.type) }
                            )
                        }
                    }
                }
            }

            Text("Location", style = MaterialTheme.typography.titleMedium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                PixyMapboxDetailMap(
                    latitude = issue.latitude,
                    longitude = issue.longitude
                )
            }

            Button(
                onClick = { issueViewModel.upvoteIssue(issue.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (issueViewModel.hasVoted(issue.id)) {
                        "Voted (${issue.votes})"
                    } else {
                        "Upvote (${issue.votes})"
                    }
                )
            }

            Text("Comments", style = MaterialTheme.typography.titleMedium)
            issue.comments.forEach {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(it.userName, style = MaterialTheme.typography.titleMedium)
                    Text(it.text)
                    Text(
                        text = formatTime(it.createdAt),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (!issueViewModel.currentUserIsAdmin) {
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Add comment") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = pixyTextFieldColors()
                )

                Button(
                    onClick = {
                        issueViewModel.addComment(issue.id, comment)
                        comment = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Post Comment")
                }

                if (issue.status == IssueStatus.RESOLVED &&
                    issue.reportedBy == issueViewModel.currentUserId
                ) {
                    Text("Feedback", style = MaterialTheme.typography.titleMedium)

                    issue.feedback.forEach {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(it.userName, style = MaterialTheme.typography.titleMedium)
                            Text(it.text)
                            Text(
                                text = formatTime(it.createdAt),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    OutlinedTextField(
                        value = feedback,
                        onValueChange = { feedback = it },
                        label = { Text("Give feedback after resolution") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = pixyTextFieldColors()
                    )

                    Button(
                        onClick = {
                            issueViewModel.addFeedback(issue.id, feedback)
                            feedback = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit Feedback")
                    }
                }
            }

            if (issueViewModel.currentUserIsAdmin && issue.fakeReports.isNotEmpty()) {
                Text("Fake Issue Reports", style = MaterialTheme.typography.titleMedium)
                issue.fakeReports.forEach { report ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text("Reason: ${report.reason.name}")
                        if (report.details.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(report.details)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("By: ${report.userName}", style = MaterialTheme.typography.bodySmall)
                        Text("At: ${formatTime(report.createdAt)}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}