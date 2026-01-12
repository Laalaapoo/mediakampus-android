package com.example.mediakampus.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediakampus.ui.AppViewModelProvider
import com.example.mediakampus.ui.screens.auth.AuthUiState
import com.example.mediakampus.ui.screens.auth.AuthViewModel

@Composable
fun SplashScreen(
    onSessionFound: (String) -> Unit, // role
    onNoSession: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(true) {
        viewModel.checkSession()
    }

    LaunchedEffect(uiState) {
        when(val state = uiState) {
            is AuthUiState.Success -> onSessionFound(state.role)
            is AuthUiState.Idle -> onNoSession() // Token kosong / error fetch profile
            is AuthUiState.Error -> onNoSession()
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}