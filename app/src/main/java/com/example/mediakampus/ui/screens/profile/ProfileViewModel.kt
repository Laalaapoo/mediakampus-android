package com.example.mediakampus.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediakampus.data.model.UserResponseDto
import com.example.mediakampus.data.model.UserUpdateDto
import com.example.mediakampus.data.repository.MediaKampusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: MediaKampusRepository) : ViewModel() {
    private val _user = MutableStateFlow<UserResponseDto?>(null)
    val user: StateFlow<UserResponseDto?> = _user.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.getMyProfile()
            if (res.isSuccess) _user.value = res.getOrNull()
            _isLoading.value = false
        }
    }

    fun updateProfile(name: String, pass: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            // Perbaikan: Pastikan memanggil updateMyProfile sesuai dengan Repository
            val res = repository.updateMyProfile(UserUpdateDto(name, if(pass.isNullOrBlank()) null else pass))
            if (res.isSuccess) {
                _message.value = "Profil diperbarui!"
                loadProfile()
            } else {
                _message.value = "Gagal update profile"
            }
            _isLoading.value = false
        }
    }
    fun clearMessage() { _message.value = null }
}