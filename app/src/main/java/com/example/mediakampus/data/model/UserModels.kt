package com.example.mediakampus.data.model

data class UserResponseDto(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    val active: Boolean
)

data class UserUpdateDto(
    val name: String?,
    val password: String?
)

data class AdminUserCreateDto(
    val name: String,
    val email: String,
    val password: String,
    val roleName: String
)