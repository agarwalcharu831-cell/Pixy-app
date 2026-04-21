package com.example.pixy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pixy.model.CommunityDrive
import com.example.pixy.model.OrganizerType
import com.example.pixy.viewmodel.IssueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    issueViewModel: IssueViewModel,
    onBack: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community Drives") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Create Drive")
            }
        }
    ) { padding ->
        val drives = issueViewModel.communityDrives

        if (drives.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No community drives available yet.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(drives) { drive ->
                    CommunityDriveCard(
                        drive = drive,
                        onJoin = { issueViewModel.joinCommunityDrive(drive.id) },
                        isJoined = drive.participants.contains(issueViewModel.currentUser.name)
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateDriveDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, desc, loc, orgType, orgName, ngoId, ngoEmail, ngoPhone ->
                issueViewModel.createCommunityDrive(
                    title, desc, loc, orgType, orgName, ngoId, ngoEmail, ngoPhone
                )
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun CommunityDriveCard(
    drive: CommunityDrive,
    onJoin: () -> Unit,
    isJoined: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = drive.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = drive.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, size = 16.dp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = drive.location, style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Groups, contentDescription = null, size = 16.dp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${drive.participants.size} participants",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Organized by: ${drive.organizerName}",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "Type: ${drive.organizerType.name}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                
                Button(
                    onClick = onJoin,
                    enabled = !isJoined,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isJoined) "Joined" else "Join Drive")
                }
            }
        }
    }
}

@Composable
private fun Icon(imageVector: ImageVector, contentDescription: String?, size: androidx.compose.ui.unit.Dp) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = Modifier.size(size)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDriveDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, OrganizerType, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var organizerName by remember { mutableStateOf("") }
    var organizerType by remember { mutableStateOf(OrganizerType.PERSON) }
    var ngoId by remember { mutableStateOf("") }
    var ngoEmail by remember { mutableStateOf("") }
    var ngoPhone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Community Drive") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = organizerName,
                        onValueChange = { organizerName = it },
                        label = { Text("Organizer Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = organizerType == OrganizerType.PERSON,
                            onClick = { organizerType = OrganizerType.PERSON }
                        )
                        Text("Individual")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = organizerType == OrganizerType.NGO,
                            onClick = { organizerType = OrganizerType.NGO }
                        )
                        Text("NGO")
                    }
                }
                if (organizerType == OrganizerType.NGO) {
                    item {
                        OutlinedTextField(
                            value = ngoId,
                            onValueChange = { ngoId = it },
                            label = { Text("NGO Registration ID") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = ngoEmail,
                            onValueChange = { ngoEmail = it },
                            label = { Text("Contact Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = ngoPhone,
                            onValueChange = { ngoPhone = it },
                            label = { Text("Contact Phone") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(title, description, location, organizerType, organizerName, ngoId, ngoEmail, ngoPhone)
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
