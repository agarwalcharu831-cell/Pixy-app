package com.example.pixy.model

data class CommunityDrive(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val organizerType: OrganizerType,
    val organizerName: String,
    val ngoRegistrationId: String = "",
    val ngoContactEmail: String = "",
    val ngoContactPhone: String = "",
    val participants: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class OrganizerType {
    PERSON,
    NGO
}