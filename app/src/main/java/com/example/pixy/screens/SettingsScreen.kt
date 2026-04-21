package com.example.pixy.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pixy.model.ThemeMode
import com.example.pixy.viewmodel.IssueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    issueViewModel: IssueViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenAccount: () -> Unit,
    onOpenHelp: () -> Unit,
    onOpenFeedback: () -> Unit
) {
    var themeExpanded by remember { mutableStateOf(false) }
    var languageExpanded by remember { mutableStateOf(false) }
    val languages = listOf("English", "Hindi", "Spanish")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Appearance", style = MaterialTheme.typography.titleMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { themeExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Theme: ${issueViewModel.themeMode.name.lowercase().replaceFirstChar { it.uppercase() }}")
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(expanded = themeExpanded, onDismissRequest = { themeExpanded = false }) {
                    ThemeMode.values().forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                issueViewModel.setTheme(mode)
                                themeExpanded = false
                            }
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { languageExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Language: ${issueViewModel.appLanguage}")
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(expanded = languageExpanded, onDismissRequest = { languageExpanded = false }) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language) },
                            onClick = {
                                issueViewModel.setLanguage(language)
                                languageExpanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = onOpenAccount,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Account")
            }

            Button(
                onClick = onOpenFeedback,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("App Feedback")
            }

            Button(
                onClick = onOpenHelp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Help & Support")
            }

            TextButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}