package com.example.pixy.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pixy.components.IssueCard
import com.example.pixy.model.Issue
import com.example.pixy.viewmodel.IssueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyIssuesScreen(
    issueViewModel: IssueViewModel,
    onBack: () -> Unit,
    onIssueClick: (String) -> Unit
) {
    val issues by issueViewModel.issues.collectAsState()
    val myIssues = issues.filter { it.reportedBy == issueViewModel.currentUserId }
    var issueToDelete by remember { mutableStateOf<Issue?>(null) }

    if (issueToDelete != null) {
        AlertDialog(
            onDismissRequest = { issueToDelete = null },
            title = { Text("Delete issue") },
            text = { Text("Are you sure you want to delete this issue?") },
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Issues") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding: PaddingValues ->
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(myIssues.sortedByDescending { it.createdAt }.size) { index ->
                val issue = myIssues.sortedByDescending { it.createdAt }[index]
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    IssueCard(
                        issue = issue,
                        onVote = { issueViewModel.upvoteIssue(issue.id) },
                        hasVoted = issueViewModel.hasVoted(issue.id),
                        onClick = { onIssueClick(issue.id) }
                    )
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillParentMaxWidth()) {
                        TextButton(onClick = { issueToDelete = issue }) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}