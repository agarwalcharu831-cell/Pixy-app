package com.example.pixy.model

data class Issue(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val address: String = "",
    val status: IssueStatus = IssueStatus.OPEN,
    val votes: Int = 0,
    val reportedBy: String = "",
    val reportedByName: String = "",
    val reportedByEmail: String = "",
    val votedUsers: List<String> = emptyList(),
    val media: List<IssueMedia> = emptyList(),
    val comments: List<IssueComment> = emptyList(),
    val fakeReports: List<FakeIssueReport> = emptyList(),
    val feedback: List<IssueFeedback> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class IssueMedia(
    val uri: String,
    val type: MediaType,
    val source: MediaSource
)

enum class MediaType {
    IMAGE, VIDEO
}

enum class MediaSource {
    CAMERA, DISK
}

data class IssueComment(
    val id: String,
    val userId: String,
    val userName: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class FakeIssueReport(
    val id: String,
    val issueId: String,
    val userId: String,
    val userName: String,
    val reason: FakeIssueReason,
    val details: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class FakeIssueReason {
    SPAM,
    DUPLICATE,
    WRONG_LOCATION,
    OFFENSIVE,
    OTHER
}

data class IssueFeedback(
    val id: String,
    val userId: String,
    val userName: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

val CATEGORIES = listOf("Pothole", "Garbage", "Water Leak", "Streetlight", "Sewage", "Other")

fun categoryIcon(category: String): String = when (category) {
    "Pothole" -> "🕳️"
    "Garbage" -> "🗑️"
    "Water Leak" -> "💧"
    "Streetlight" -> "💡"
    "Sewage" -> "🚰"
    else -> "⚠️"
}