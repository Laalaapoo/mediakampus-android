package com.example.mediakampus.data.model

data class Assignment(
    val assignmentId: Long,
    val request: RequestResponseDto, // Menggunakan RequestResponseDto agar sinkron dengan API
    val anggota: UserResponseDto,
    val assignedAt: String
)

data class AssignmentCreateDto(
    val requestId: Long,
    val anggotaId: Long
)

data class WorkResult(
    val resultId: Long,
    val assignment: Assignment?,
    val fileUrl: String,
    val notes: String?,
    val submittedAt: String
)

data class WorkResultCreateDto(
    val assignmentId: Long,
    val fileUrl: String,
    val notes: String
)