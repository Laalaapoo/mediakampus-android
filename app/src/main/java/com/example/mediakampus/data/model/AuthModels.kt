package com.example.mediakampus.data.model

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)
// Backend mengembalikan token string mentah, kita handle di Retrofit