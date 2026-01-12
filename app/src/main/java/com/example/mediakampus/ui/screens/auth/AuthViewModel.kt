package com.example.mediakampus.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediakampus.data.datastore.UserPreferences
import com.example.mediakampus.data.model.LoginRequest
import com.example.mediakampus.data.model.RegisterRequest
import com.example.mediakampus.data.repository.MediaKampusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val role: String) : AuthUiState() // Role menentukan arah navigasi
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val repository: MediaKampusRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Cek session saat aplikasi dibuka (Splash Screen)
    fun checkSession() {
        viewModelScope.launch {
            val token = userPreferences.authToken.first()
            if (!token.isNullOrEmpty()) {
                // Jika token ada, cek role yang tersimpan atau fetch ulang profile
                fetchMyProfileAndNavigate()
            } else {
                _uiState.value = AuthUiState.Idle // Tetap di login/idle
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                // 1. Login untuk dapat Token
                val loginResult = repository.login(LoginRequest(email, pass))

                if (loginResult.isSuccess) {
                    val token = loginResult.getOrNull() ?: ""
                    userPreferences.saveAuthToken(token)

                    // 2. Ambil Profile untuk tahu Role (karena login cm return token string)
                    fetchMyProfileAndNavigate()
                } else {
                    _uiState.value = AuthUiState.Error("Login Gagal: Cek email/password")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.register(RegisterRequest(name, email, pass))
            if (result.isSuccess) {
                // Auto login atau minta user login? Kita arahkan ke login saja biar aman
                _uiState.value = AuthUiState.Error("Registrasi Berhasil! Silakan Login.") // Pakai Error state buat nampilin snackbar sementara :D atau buat state khusus
            } else {
                _uiState.value = AuthUiState.Error("Registrasi Gagal: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    private suspend fun fetchMyProfileAndNavigate() {
        val profileResult = repository.getMyProfile()
        if (profileResult.isSuccess) {
            val user = profileResult.getOrNull()
            if (user != null) {
                userPreferences.saveUserProfile(user.name, user.role)
                _uiState.value = AuthUiState.Success(user.role)
            } else {
                _uiState.value = AuthUiState.Error("Gagal mengambil profil user")
            }
        } else {
            // Token mungkin expired
            userPreferences.clearSession()
            _uiState.value = AuthUiState.Error("Sesi habis, silakan login ulang")
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}