package com.example.mediakampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mediakampus.ui.navigation.MediaKampusNavHost
// Jika tema error, hapus baris import theme di bawah ini sementara
// import com.example.mediakampus.ui.theme.MediaKampusTheme

class MainActivity : ComponentActivity() { // Perhatikan: ComponentActivity, bukan AppCompatActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Gunakan MaterialTheme default jika theme custom belum dibuat
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Panggil Navigasi Utama kita
                    MediaKampusNavHost()
                }
            }
        }
    }
}