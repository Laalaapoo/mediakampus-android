package com.example.mediakampus.ui.screens.dashboard

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediakampus.data.model.RequestResponseDto
import com.example.mediakampus.ui.AppViewModelProvider
import com.example.mediakampus.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PemohonScreen(
    onLogout: () -> Unit,
    viewModel: PemohonViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val requests by viewModel.requests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadData() }

    if (message != null) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.clearMessage()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Halo, ${profile?.name ?: "User"}") }, actions = {
                Button(onClick = onLogout) { Text("Logout") }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Buat Request")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(requests) { req ->
                    RequestItem(req)
                }
            }
        }
    }

    if (showDialog) {
        CreateRequestDialog(
            onDismiss = { showDialog = false },
            onSubmit = { t, d, dt, div ->
                viewModel.createRequest(t, d, dt, div)
                showDialog = false
            }
        )
    }
}

@Composable
fun RequestItem(req: RequestResponseDto) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = req.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "Status: ${req.status}", color = Color.Blue)
            Text(text = "Waktu: ${req.eventTime}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun CreateRequestDialog(onDismiss: () -> Unit, onSubmit: (String, String, String, List<Long>) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026-01-01T09:00:00") }
    val selectedDivisions = remember { mutableStateListOf<Long>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Request Baru") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") })
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("ISO Date (T)") })
                Constants.DIVISION_MAP.forEach { (id, name) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = selectedDivisions.contains(id), onCheckedChange = { if(it) selectedDivisions.add(id) else selectedDivisions.remove(id) })
                        Text(text = name)
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onSubmit(title, desc, date, selectedDivisions) }) { Text("Kirim") } }
    )
}