package com.example.pixy.model

enum class IssueStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED;

    fun label(): String = when (this) {
        OPEN -> "Open"
        IN_PROGRESS -> "In Progress"
        RESOLVED -> "✓ Resolved"
    }
}