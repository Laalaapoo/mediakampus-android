package com.example.mediakampus.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediakampus.ui.AppViewModelProvider

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Error) {
            val msg = (uiState as AuthUiState.Error).message
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            // Jika sukses (pesan sukses dikirim lewat Error state hack tadi), kembali ke login
            if (msg.contains("Berhasil", ignoreCase = true)) {
                onNavigateBack()
            }
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Daftar Pemohon", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name, onValueChange = { name = it }, label = { Text("Nama Lengkap") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it }, label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password, onValueChange = { password = it }, label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.register(name, email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is AuthUiState.Loading
        ) {
            Text(if (uiState is AuthUiState.Loading) "Loading..." else "Daftar")
        }

        TextButton(onClick = onNavigateBack) {
            Text("Kembali ke Login")
        }
    }
}