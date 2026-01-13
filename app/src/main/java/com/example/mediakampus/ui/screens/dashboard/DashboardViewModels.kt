package com.example.mediakampus.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediakampus.data.model.*
import com.example.mediakampus.data.repository.MediaKampusRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// --- PEMOHON ---
class PemohonViewModel(private val repository: MediaKampusRepository) : ViewModel() {
    private val _requests = MutableStateFlow<List<RequestResponseDto>>(emptyList())
    val requests = _requests.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow() // Akses publik

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow() // Akses publik

    private val _profile = MutableStateFlow<UserResponseDto?>(null)
    val profile = _profile.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getMyProfile().onSuccess { _profile.value = it }
            repository.getPemohonRequests().onSuccess { _requests.value = it }
            _isLoading.value = false
        }
    }

    fun loadRequests() = loadData()

    fun createRequest(title: String, desc: String, date: String, divisions: List<Long>) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createRequest(RequestCreateDto(title, desc, date, divisions))
                .onSuccess { _message.value = "Berhasil!"; loadData() }
                .onFailure { _message.value = "Gagal: ${it.message}" }
            _isLoading.value = false
        }
    }
    fun clearMessage() { _message.value = null }
}

// --- ANGGOTA VIEW MODEL ---
class AnggotaViewModel(private val repository: MediaKampusRepository) : ViewModel() {
    private val _assignments = MutableStateFlow<List<Assignment>>(emptyList())
    val assignments = _assignments.asStateFlow()

    private val _history = MutableStateFlow<List<WorkResult>>(emptyList())
    val history = _history.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            // Load Tugas
            repository.getAssignments()
                .onSuccess {
                    _assignments.value = it
                    if (it.isEmpty()) _message.value = "Belum ada tugas baru"
                }
                .onFailure { _message.value = "Gagal memuat tugas: ${it.message}" }

            // Load Riwayat
            repository.getMyResults()
                .onSuccess { _history.value = it }
                .onFailure { _message.value = "Gagal memuat riwayat: ${it.message}" }

            _isLoading.value = false
        }
    }

    fun submitWork(assignmentId: Long, url: String, notes: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.submitResult(WorkResultCreateDto(assignmentId, url, notes))
                .onSuccess {
                    _message.value = "Berhasil mengirim hasil!"
                    loadData() // Refresh data setelah submit
                }
                .onFailure { _message.value = "Gagal kirim: ${it.message}" }
            _isLoading.value = false
        }
    }
    fun clearMessage() { _message.value = null }
}

// --- ADMIN VIEW MODEL ---
class AdminViewModel(private val repository: MediaKampusRepository) : ViewModel() {
    private val _requests = MutableStateFlow<List<Request>>(emptyList())
    val requests = _requests.asStateFlow()

    private val _allUsers = MutableStateFlow<List<UserResponseDto>>(emptyList())
    val allUsers = _allUsers.asStateFlow()

    private val _members = MutableStateFlow<List<UserResponseDto>>(emptyList())
    val members = _members.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            // Load Request
            repository.getAllRequests()
                .onSuccess {
                    _requests.value = it
                    if (it.isEmpty()) _message.value = "Belum ada request masuk"
                }
                .onFailure { _message.value = "Gagal load request: ${it.message}" }

            // Load Anggota untuk Dropdown
            repository.getUsers("ANGGOTA").onSuccess { _members.value = it }
            _isLoading.value = false
        }
    }

    fun loadAllUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getUsers(null)
                .onSuccess { _allUsers.value = it }
                .onFailure { _message.value = "Gagal load user: ${it.message}" }
            _isLoading.value = false
        }
    }

    fun toggleUserActive(userId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.updateUserStatus(userId, !currentStatus)
                .onSuccess { loadAllUsers() }
                .onFailure { _message.value = "Gagal update status: ${it.message}" }
        }
    }

    fun createUser(n: String, e: String, p: String, r: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createUser(AdminUserCreateDto(n, e, p, r))
                .onSuccess {
                    _message.value = "User $n berhasil dibuat"
                    loadAllUsers()
                }
                .onFailure { _message.value = "Gagal buat user: ${it.message}" }
            _isLoading.value = false
        }
    }

    fun updateStatus(id: Long, status: String, reason: String?) {
        viewModelScope.launch {
            repository.updateRequestStatus(id, RequestStatusUpdateDto(status, reason))
                .onSuccess {
                    _message.value = "Status diupdate!"
                    loadData()
                }
                .onFailure { _message.value = "Gagal: ${it.message}" }
        }
    }

    fun assignMember(reqId: Long, userId: Long) {
        viewModelScope.launch {
            repository.assignMember(AssignmentCreateDto(reqId, userId))
                .onSuccess {
                    _message.value = "Anggota ditugaskan!"
                    loadData()
                }
                .onFailure { _message.value = "Gagal assign: ${it.message}" }
        }
    }
    fun clearMessage() { _message.value = null }
}