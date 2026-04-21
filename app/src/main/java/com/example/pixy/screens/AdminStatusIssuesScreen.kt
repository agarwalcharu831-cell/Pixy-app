package com.example.pixy.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pixy.components.AdminIssueCard
import com.example.pixy.model.IssueStatus
import com.example.pixy.viewmodel.IssueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStatusIssuesScreen(
    issueViewModel: IssueViewModel,
    status: IssueStatus,
    onBack: () -> Unit,
    onIssueClick: (String) -> Unit
) {
    val filtered = issueViewModel.getIssuesByStatus(status)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(status.label()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
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
            items(filtered) { issue ->
                Column(modifier = Modifier.clickable { onIssueClick(issue.id) }) {
                    AdminIssueCard(
                        issue = issue,
                        issueViewModel = issueViewModel,
                        onDelete = { issueViewModel.deleteIssue(issue.id) }
                    )
                }
            }
        }
    }
}