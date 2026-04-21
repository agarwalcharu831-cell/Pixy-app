package com.example.pixy.data

import com.example.pixy.model.FakeIssueReport
import com.example.pixy.model.FakeIssueReason
import com.example.pixy.model.Issue
import com.example.pixy.model.IssueComment
import com.example.pixy.model.IssueFeedback
import com.example.pixy.model.IssueMedia
import com.example.pixy.model.IssueStatus
import com.example.pixy.model.MediaSource
import com.example.pixy.model.MediaType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object IssueRepository {

    private val _issues = MutableStateFlow(
        listOf(
            Issue(
                id = UUID.randomUUID().toString(),
                title = "Large pothole on MG Road",
                category = "Pothole",
                description = "Deep pothole causing vehicle damage near bus stop.",
                latitude = 28.6139,
                longitude = 77.2090,
                address = "MG Road, New Delhi",
                status = IssueStatus.OPEN,
                votes = 14,
                reportedBy = "user_201",
                reportedByName = "Aarav",
                reportedByEmail = "aarav@example.com",
                comments = listOf(
                    IssueComment(
                        id = UUID.randomUUID().toString(),
                        userId = "user_201",
                        userName = "Aarav",
                        text = "This is still there today."
                    )
                )
            ),
            Issue(
                id = UUID.randomUUID().toString(),
                title = "Garbage pile near park entrance",
                category = "Garbage",
                description = "Uncollected garbage for 3 days, foul smell spreading.",
                latitude = 28.5931,
                longitude = 77.2271,
                address = "Lodi Garden, Delhi",
                status = IssueStatus.IN_PROGRESS,
                votes = 9,
                reportedBy = "user_202",
                reportedByName = "Ishita",
                reportedByEmail = "ishita@example.com"
            ),
            Issue(
                id = UUID.randomUUID().toString(),
                title = "Water leaking from main pipeline",
                category = "Water Leak",
                description = "Continuous water leak wasting thousands of litres daily.",
                latitude = 28.6315,
                longitude = 77.2167,
                address = "Connaught Place, Delhi",
                status = IssueStatus.OPEN,
                votes = 22,
                reportedBy = "user_203",
                reportedByName = "Karan",
                reportedByEmail = "karan@example.com",
                media = listOf(
                    IssueMedia(
                        uri = "",
                        type = MediaType.IMAGE,
                        source = MediaSource.DISK
                    )
                )
            )
        )
    )

    fun getIssues(): StateFlow<List<Issue>> = _issues.asStateFlow()

    fun addIssue(issue: Issue) {
        _issues.value = listOf(issue) + _issues.value
    }

    fun getIssueById(id: String): Issue? = _issues.value.find { it.id == id }

    fun getIssuesByStatus(status: IssueStatus): List<Issue> =
        _issues.value.filter { it.status == status }

    fun upvoteIssue(issueId: String, userId: String) {
        _issues.value = _issues.value.map { issue ->
            if (issue.id == issueId) {
                if (issue.votedUsers.contains(userId)) {
                    issue.copy(
                        votes = (issue.votes - 1).coerceAtLeast(0),
                        votedUsers = issue.votedUsers - userId
                    )
                } else {
                    issue.copy(
                        votes = issue.votes + 1,
                        votedUsers = issue.votedUsers + userId
                    )
                }
            } else issue
        }
    }

    fun addComment(issueId: String, comment: IssueComment) {
        _issues.value = _issues.value.map { issue ->
            if (issue.id == issueId) issue.copy(comments = issue.comments + comment) else issue
        }
    }

    fun addFeedback(issueId: String, feedback: IssueFeedback) {
        _issues.value = _issues.value.map { issue ->
            if (issue.id == issueId) issue.copy(feedback = issue.feedback + feedback) else issue
        }
    }

    fun addFakeReport(
        issueId: String,
        userId: String,
        userName: String,
        reason: FakeIssueReason,
        details: String
    ) {
        _issues.value = _issues.value.map { issue ->
            if (issue.id == issueId) {
                issue.copy(
                    fakeReports = issue.fakeReports + FakeIssueReport(
                        id = UUID.randomUUID().toString(),
                        issueId = issueId,
                        userId = userId,
                        userName = userName,
                        reason = reason,
                        details = details
                    )
                )
            } else issue
        }
    }

    fun updateIssueStatus(issueId: String, newStatus: IssueStatus) {
        _issues.value = _issues.value.map { issue ->
            if (issue.id == issueId) issue.copy(status = newStatus) else issue
        }
    }

    fun deleteIssue(issueId: String) {
        _issues.value = _issues.value.filterNot { it.id == issueId }
    }
}