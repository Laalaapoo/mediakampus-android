package com.example.mediakampus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mediakampus.ui.AppViewModelProvider
import com.example.mediakampus.ui.screens.SplashScreen
import com.example.mediakampus.ui.screens.auth.AuthViewModel
import com.example.mediakampus.ui.screens.auth.LoginScreen
import com.example.mediakampus.ui.screens.auth.RegisterScreen
import com.example.mediakampus.ui.screens.dashboard.AdminScreen
import com.example.mediakampus.ui.screens.dashboard.AnggotaScreen
import com.example.mediakampus.ui.screens.dashboard.PemohonScreen
// ------------------------------------------

enum class MediaKampusScreen(val route: String) {
    Splash("splash"),
    Login("login"),
    Register("register"),
    DashboardPemohon("dashboard_pemohon"),
    DashboardAnggota("dashboard_anggota"),
    DashboardAdmin("dashboard_admin")
}

@Composable
fun MediaKampusNavHost(
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val doLogout = {
        authViewModel.resetState()
        navController.navigate(MediaKampusScreen.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = MediaKampusScreen.Splash.route
    ) {
        composable(MediaKampusScreen.Splash.route) {
            SplashScreen(
                onSessionFound = { role -> navigateBasedOnRole(navController, role) },
                onNoSession = {
                    navController.navigate(MediaKampusScreen.Login.route) {
                        popUpTo(MediaKampusScreen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(MediaKampusScreen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role -> navigateBasedOnRole(navController, role) },
                onNavigateToRegister = { navController.navigate(MediaKampusScreen.Register.route) }
            )
        }

        composable(MediaKampusScreen.Register.route) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(MediaKampusScreen.DashboardPemohon.route) {
            PemohonScreen(onLogout = doLogout)
        }

        composable(MediaKampusScreen.DashboardAnggota.route) {
            AnggotaScreen(onLogout = doLogout)
        }

        composable(MediaKampusScreen.DashboardAdmin.route) {
            AdminScreen(onLogout = doLogout)
        }
    }
}

fun navigateBasedOnRole(navController: NavHostController, role: String) {
    val route = when (role.uppercase()) {
        "PEMOHON" -> MediaKampusScreen.DashboardPemohon.route
        "ANGGOTA" -> MediaKampusScreen.DashboardAnggota.route
        "ADMIN" -> MediaKampusScreen.DashboardAdmin.route
        else -> MediaKampusScreen.Login.route
    }
    navController.navigate(route) {
        popUpTo(MediaKampusScreen.Splash.route) { inclusive = true }
        popUpTo(MediaKampusScreen.Login.route) { inclusive = true }
    }
}