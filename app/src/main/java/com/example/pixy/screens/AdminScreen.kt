package com.example.pixy.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pixy.components.AdminIssueCard
import com.example.pixy.model.Issue
import com.example.pixy.model.IssueStatus
import com.example.pixy.viewmodel.IssueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    issueViewModel: IssueViewModel,
    onBack: () -> Unit,
    onIssueClick: (String) -> Unit,
    onStatusClick: (IssueStatus) -> Unit
) {
    val issues by issueViewModel.issues.collectAsState()
    var issueToDelete by remember { mutableStateOf<Issue?>(null) }

    if (issueToDelete != null) {
        AlertDialog(
            onDismissRequest = { issueToDelete = null },
            title = { Text("Delete issue") },
            text = { Text("Are you sure you want to delete this issue from the dashboard?") },
            confirmButton = {
                Button(onClick = {
                    issueViewModel.deleteIssue(issueToDelete!!.id)
                    issueToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { issueToDelete = null }) {
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
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        AnimatedContent(
            targetState = issues.size,
            label = "dashboard_anim"
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatusStatCard("Open", openCount, Modifier.weight(1f)) {
                            onStatusClick(IssueStatus.OPEN)
                        }
                        StatusStatCard("In Progress", progressCount, Modifier.weight(1f)) {
                            onStatusClick(IssueStatus.IN_PROGRESS)
                        }
                        StatusStatCard("Resolved", resolvedCount, Modifier.weight(1f)) {
                            onStatusClick(IssueStatus.RESOLVED)
                        }
                    }
                    Spacer(Modifier.padding(4.dp))
                }

                items(issues.sortedByDescending { it.votes }) { issue ->
                    Column(modifier = Modifier.clickable { onIssueClick(issue.id) }) {
                        AdminIssueCard(
                            issue = issue,
                            issueViewModel = issueViewModel,
                            onDelete = { issueToDelete = issue }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusStatCard(
    label: String,
    count: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Text("$count", style = MaterialTheme.typography.headlineSmall)
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}