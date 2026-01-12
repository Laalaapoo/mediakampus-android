package com.example.mediakampus.data.repository

import com.example.mediakampus.data.model.*
import com.example.mediakampus.data.network.ApiService
import retrofit2.Response

class MediaKampusRepository(private val apiService: ApiService) {
    // Auth & Profile
    suspend fun login(req: LoginRequest) = safeApiCall { apiService.login(req) }
    suspend fun register(req: RegisterRequest) = safeApiCall { apiService.register(req) }
    suspend fun getMyProfile() = safeApiCall { apiService.getMyProfile() }
    suspend fun updateMyProfile(req: UserUpdateDto) = safeApiCall { apiService.updateMyProfile(req) }

    // Pemohon
    suspend fun getPemohonRequests() = safeApiCall { apiService.getPemohonRequests() }
    suspend fun createRequest(req: RequestCreateDto) = safeApiCall { apiService.createRequest(req) }

    // Anggota
    suspend fun getAssignments(status: String? = null) = safeApiCall { apiService.getAssignments(status) }
    suspend fun getMyResults() = safeApiCall { apiService.getMyWorkResults() }
    suspend fun submitResult(req: WorkResultCreateDto) = safeApiCall { apiService.submitWorkResult(req) }

    // Admin
    suspend fun getAllRequests() = safeApiCall { apiService.getAllRequests() }
    suspend fun updateRequestStatus(id: Long, req: RequestStatusUpdateDto) = safeApiCall { apiService.updateRequestStatus(id, req) }
    suspend fun getUsers(role: String?) = safeApiCall { apiService.getUsers(role) }
    suspend fun createUser(req: AdminUserCreateDto) = safeApiCall { apiService.createUser(req) }
    suspend fun updateUserStatus(id: Long, active: Boolean) = safeApiCall { apiService.updateUserStatus(id, active) }
    suspend fun assignMember(req: AssignmentCreateDto) = safeApiCall { apiService.assignMember(req) }
    suspend fun getAssignmentResults(asgId: Long) = safeApiCall { apiService.getAssignmentResults(asgId) }
}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) Result.success(body)
            else Result.failure(Exception("Body is null"))
        } else {
            Result.failure(Exception("Error: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}