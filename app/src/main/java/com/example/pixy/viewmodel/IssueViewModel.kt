package com.example.pixy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.pixy.data.IssueRepository
import com.example.pixy.model.CommunityDrive
import com.example.pixy.model.FakeIssueReason
import com.example.pixy.model.Issue
import com.example.pixy.model.IssueComment
import com.example.pixy.model.IssueFeedback
import com.example.pixy.model.IssueMedia
import com.example.pixy.model.IssueStatus
import com.example.pixy.model.OrganizerType
import com.example.pixy.model.ThemeMode
import com.example.pixy.model.User
import com.example.pixy.model.UserRole
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class IssueViewModel : ViewModel() {

    val issues: StateFlow<List<Issue>> = IssueRepository.getIssues()

    val communityDrives = mutableStateListOf(
        CommunityDrive(
            id = UUID.randomUUID().toString(),
            title = "Weekend Clean-Up Drive",
            description = "Join us to clean the market road and park surroundings.",
            location = "Sector 8 Community Park",
            organizerType = OrganizerType.PERSON,
            organizerName = "Local Volunteers",
            participants = listOf("Aarav", "Ishita")
        )
    )

    var currentUser by mutableStateOf(
        User(
            id = "guest_user",
            name = "Guest",
            email = "guest@pixy.app",
            role = UserRole.USER
        )
    )
        private set

    var selectedRole by mutableStateOf<UserRole?>(null)
        private set

    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
        private set

    var appLanguage by mutableStateOf("English")
        private set

    val currentUserId: String get() = currentUser.id
    val currentUserIsAdmin: Boolean get() = currentUser.role == UserRole.ADMIN

    fun setRole(role: UserRole) {
        selectedRole = role
    }

    fun loginManual(name: String, email: String) {
        currentUser = User(
            id = UUID.randomUUID().toString(),
            name = name.ifBlank { "User" },
            email = email,
            role = selectedRole ?: UserRole.USER,
            profileImageUrl = currentUser.profileImageUrl
        )
    }

    fun loginWithGoogle() {
        currentUser = User(
            id = UUID.randomUUID().toString(),
            name = if (selectedRole == UserRole.ADMIN) "Google Admin" else "Google User",
            email = if (selectedRole == UserRole.ADMIN) "admin@pixy.app" else "user@pixy.app",
            role = selectedRole ?: UserRole.USER,
            profileImageUrl = currentUser.profileImageUrl
        )
    }

    fun logout() {
        selectedRole = null
        currentUser = User(
            id = "guest_user",
            name = "Guest",
            email = "guest@pixy.app",
            role = UserRole.USER
        )
    }

    fun updateAccount(
        name: String,
        email: String,
        password: String,
        address: String,
        phoneNumber: String,
        profileImageUrl: String
    ) {
        currentUser = currentUser.copy(
            name = name,
            email = email,
            password = password,
            address = address,
            phoneNumber = phoneNumber,
            profileImageUrl = profileImageUrl
        )
    }

    fun setTheme(mode: ThemeMode) {
        themeMode = mode
    }

    fun setLanguage(language: String) {
        appLanguage = language
    }

    fun submitIssue(
        title: String,
        category: String,
        description: String,
        latitude: Double,
        longitude: Double,
        address: String = "",
        media: List<IssueMedia> = emptyList()
    ) {
        val newIssue = Issue(
            id = UUID.randomUUID().toString(),
            title = title,
            category = category,
            description = description,
            latitude = latitude,
            longitude = longitude,
            address = address,
            media = media,
            reportedBy = currentUserId,
            reportedByName = currentUser.name,
            reportedByEmail = currentUser.email
        )
        IssueRepository.addIssue(newIssue)
    }

    fun getIssueById(issueId: String): Issue? = IssueRepository.getIssueById(issueId)

    fun getIssuesByStatus(status: IssueStatus): List<Issue> =
        IssueRepository.getIssuesByStatus(status)

    fun upvoteIssue(issueId: String) {
        IssueRepository.upvoteIssue(issueId, currentUserId)
    }

    fun hasVoted(issueId: String): Boolean {
        return IssueRepository.getIssueById(issueId)?.votedUsers?.contains(currentUserId) == true
    }

    fun addComment(issueId: String, text: String) {
        if (text.isBlank()) return
        IssueRepository.addComment(
            issueId = issueId,
            comment = IssueComment(
                id = UUID.randomUUID().toString(),
                userId = currentUser.id,
                userName = currentUser.name,
                text = text.trim()
            )
        )
    }

    fun addFeedback(issueId: String, text: String) {
        if (text.isBlank()) return
        IssueRepository.addFeedback(
            issueId = issueId,
            feedback = IssueFeedback(
                id = UUID.randomUUID().toString(),
                userId = currentUser.id,
                userName = currentUser.name,
                text = text.trim()
            )
        )
    }

    fun reportIssueFake(issueId: String, reason: FakeIssueReason, details: String) {
        IssueRepository.addFakeReport(
            issueId = issueId,
            userId = currentUser.id,
            userName = currentUser.name,
            reason = reason,
            details = details
        )
    }

    fun updateStatus(issueId: String, status: IssueStatus) {
        IssueRepository.updateIssueStatus(issueId, status)
    }

    fun deleteIssue(issueId: String) {
        IssueRepository.deleteIssue(issueId)
    }

    fun createCommunityDrive(
        title: String,
        description: String,
        location: String,
        organizerType: OrganizerType,
        organizerName: String,
        ngoRegistrationId: String,
        ngoContactEmail: String,
        ngoContactPhone: String
    ) {
        communityDrives.add(
            0,
            CommunityDrive(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                location = location,
                organizerType = organizerType,
                organizerName = organizerName,
                ngoRegistrationId = ngoRegistrationId,
                ngoContactEmail = ngoContactEmail,
                ngoContactPhone = ngoContactPhone,
                participants = listOf(currentUser.name)
            )
        )
    }

    fun joinCommunityDrive(driveId: String) {
        val index = communityDrives.indexOfFirst { it.id == driveId }
        if (index == -1) return
        val drive = communityDrives[index]
        if (drive.participants.contains(currentUser.name)) return
        communityDrives[index] = drive.copy(
            participants = drive.participants + currentUser.name
        )
    }
}