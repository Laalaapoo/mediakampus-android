package com.example.mediakampus.data.model

data class Request(
    val requestId: Long,
    val title: String,
    val description: String,
    val eventTime: String, // ISO-8601
    val status: String,
    val rejectionReason: String?,
    val createdAt: String,
    val pemohon: UserResponseDto,
    val divisions: List<Division> = emptyList()
)

data class Division(
    val divisionId: Long,
    val name: String
)

data class RequestCreateDto(
    val title: String,
    val description: String,
    val eventTime: String,
    val divisionIds: List<Long>
)

data class RequestResponseDto(
    val requestId: Long,
    val title: String,
    val description: String,
    val eventTime: String,
    val status: String,
    val rejectionReason: String?,
    val createdAt: String
)

data class RequestStatusUpdateDto(
    val status: String,
    val rejectionReason: String? = null
)