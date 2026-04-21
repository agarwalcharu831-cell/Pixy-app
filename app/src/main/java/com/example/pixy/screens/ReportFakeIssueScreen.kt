package com.example.pixy.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pixy.model.FakeIssueReason
import com.example.pixy.viewmodel.IssueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFakeIssueScreen(
    issueId: String,
    issueViewModel: IssueViewModel,
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    var selectedReason by remember { mutableStateOf<FakeIssueReason?>(null) }
    var details by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Fake Issue") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FakeIssueReason.values().forEach { reason ->
                FilterChip(
                    selected = selectedReason == reason,
                    onClick = { selectedReason = reason },
                    label = { Text(reason.name.replace("_", " ")) }
                )
            }

            if (selectedReason == FakeIssueReason.OTHER) {
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Details") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = pixyTextFieldColors()
                )
            }

            if (error.isNotBlank()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    when {
                        selectedReason == null -> error = "Please select a reason"
                        selectedReason == FakeIssueReason.OTHER && details.isBlank() ->
                            error = "Please enter details for Other"
                        else -> {
                            issueViewModel.reportIssueFake(
                                issueId = issueId,
                                reason = selectedReason!!,
                                details = details
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