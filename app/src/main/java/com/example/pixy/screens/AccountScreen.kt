package com.example.pixy.screens

import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.pixy.viewmodel.IssueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    issueViewModel: IssueViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(issueViewModel.currentUser.name) }
    var email by remember { mutableStateOf(issueViewModel.currentUser.email) }
    var password by remember { mutableStateOf(issueViewModel.currentUser.password) }
    var address by remember { mutableStateOf(issueViewModel.currentUser.address) }
    var phoneNumber by remember { mutableStateOf(issueViewModel.currentUser.phoneNumber) }
    var profileImageUrl by remember { mutableStateOf(issueViewModel.currentUser.profileImageUrl) }
    var error by remember { mutableStateOf("") }
    var success by remember { mutableStateOf("") }

    val profilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { profileImageUrl = it.toString() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account") },
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(120.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                AsyncImage(
                    model = profileImageUrl.ifBlank { null },
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            Button(
                onClick = {
                    profilePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Text("Change Profile Picture")
            }

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    error = ""
                    success = ""
                },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    error = ""
                    success = ""
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    error = ""
                    success = ""
                },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = address,
                onValueChange = {
                    address = it
                    error = ""
                    success = ""
                },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it
                    error = ""
                    success = ""
                },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            if (error.isNotBlank()) {
                Text(error, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }

            if (success.isNotBlank()) {
                Text(success, color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
            }

            Button(
                onClick = {
                    when {
                        name.isBlank() -> error = "Please enter your name"
                        email.isBlank() -> error = "Please enter your email"
                        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                            error = "Please enter a valid email"
                        phoneNumber.isNotBlank() && phoneNumber.length < 10 ->
                            error = "Please enter a valid phone number"
                        else -> {
                            issueViewModel.updateAccount(
                                name = name.trim(),
                                email = email.trim(),
                                password = password,
                                address = address.trim(),
                                phoneNumber = phoneNumber.trim(),
                                profileImageUrl = profileImageUrl
                            )
                            success = "Account updated"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}