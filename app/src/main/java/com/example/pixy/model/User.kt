package com.example.pixy.model

enum class UserRole {
    USER, ADMIN
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole = UserRole.USER,
    val phoneNumber: String = "",
    val address: String = "",
    val password: String = "",
    val profileImageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)