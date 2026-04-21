package com.example.pixy.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pixy.components.IssueCard
import com.example.pixy.viewmodel.IssueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserIssuesScreen(
    issueViewModel: IssueViewModel,
    onIssueClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val issues by issueViewModel.issues.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Issues") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(issues.sortedByDescending { it.votes }) { issue ->
                IssueCard(
                    issue = issue,
                    onVote = { issueViewModel.upvoteIssue(issue.id) },
                    hasVoted = issueViewModel.hasVoted(issue.id),
                    onClick = { onIssueClick(issue.id) }
                )
            }
        }
    }
}