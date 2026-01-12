package com.example.mediakampus.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mediakampus.MediaKampusApplication
import com.example.mediakampus.ui.screens.auth.AuthViewModel
import com.example.mediakampus.ui.screens.dashboard.AdminViewModel
import com.example.mediakampus.ui.screens.dashboard.AnggotaViewModel
import com.example.mediakampus.ui.screens.dashboard.PemohonViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // 1. Initializer untuk AuthViewModel (Butuh Repository & Preferences)
        initializer {
            val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MediaKampusApplication)
            AuthViewModel(
                repository = app.container.repository,
                userPreferences = app.container.userPreferences
            )
        }

        // 2. Initializer untuk PemohonViewModel (Butuh Repository)
        initializer {
            val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MediaKampusApplication)
            PemohonViewModel(app.container.repository)
        }

        // 3. Initializer untuk AnggotaViewModel (Butuh Repository)
        initializer {
            val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MediaKampusApplication)
            AnggotaViewModel(app.container.repository)
        }

        // 4. Initializer untuk AdminViewModel (Butuh Repository)
        initializer {
            val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MediaKampusApplication)
            AdminViewModel(app.container.repository)
        }
    }
}