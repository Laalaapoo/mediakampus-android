package com.example.mediakampus.data.network

import com.example.mediakampus.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // --- AUTH ---
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<String>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<String>

    // --- USERS ---
    @GET("api/users/me")
    suspend fun getMyProfile(): Response<UserResponseDto>

    @PUT("api/users/me")
    suspend fun updateMyProfile(@Body request: UserUpdateDto): Response<UserResponseDto>

    // --- PEMOHON ---
    @GET("api/pemohon/request")
    suspend fun getPemohonRequests(): Response<List<RequestResponseDto>>

    @POST("api/pemohon/request")
    suspend fun createRequest(@Body request: RequestCreateDto): Response<RequestResponseDto>

    // --- ANGGOTA ---
    @GET("api/anggota/assignments")
    suspend fun getAssignments(@Query("status") status: String? = null): Response<List<Assignment>>

    @GET("api/anggota/result")
    suspend fun getMyWorkResults(): Response<List<WorkResult>>

    @POST("api/anggota/result")
    suspend fun submitWorkResult(@Body request: WorkResultCreateDto): Response<WorkResult>

    // --- ADMIN ---
    @GET("api/admin/requests")
    suspend fun getAllRequests(): Response<List<Request>>

    @PUT("api/admin/requests/{id}/status")
    suspend fun updateRequestStatus(
        @Path("id") requestId: Long,
        @Body statusDto: RequestStatusUpdateDto
    ): Response<Request>

    @POST("api/admin/assign")
    suspend fun assignMember(@Body request: AssignmentCreateDto): Response<String>

    @GET("api/admin/users")
    suspend fun getUsers(@Query("role") role: String? = null): Response<List<UserResponseDto>>

    @POST("api/admin/users")
    suspend fun createUser(@Body request: AdminUserCreateDto): Response<UserResponseDto>

    @PUT("api/admin/users/{id}/status")
    suspend fun updateUserStatus(
        @Path("id") userId: Long,
        @Query("active") active: Boolean
    ): Response<UserResponseDto>

    @GET("api/admin/assignments/{id}/results")
    suspend fun getAssignmentResults(@Path("id") assignmentId: Long): Response<List<WorkResult>>
}