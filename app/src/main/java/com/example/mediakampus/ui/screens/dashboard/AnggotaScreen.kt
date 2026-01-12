package com.example.mediakampus.ui.screens.dashboard

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediakampus.data.model.Assignment
import com.example.mediakampus.data.model.WorkResult
import com.example.mediakampus.ui.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnggotaScreen(
    onLogout: () -> Unit,
    viewModel: AnggotaViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val assignments by viewModel.assignments.collectAsState()
    val history by viewModel.history.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    val context = LocalContext.current

    var tabIndex by remember { mutableIntStateOf(0) }
    var selectedAssignment by remember { mutableStateOf<Assignment?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    if (message != null) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.clearMessage()
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Portal Anggota") }, actions = {
                    Button(onClick = onLogout) { Text("Logout") }
                })
                TabRow(selectedTabIndex = tabIndex) {
                    Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Tugas") })
                    Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Riwayat") })
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            if (tabIndex == 0) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(assignments) { asg ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(asg.request.title, style = MaterialTheme.typography.titleMedium)
                                Text("Waktu: ${asg.request.eventTime}")
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { selectedAssignment = asg }) {
                                    Text("Submit Hasil")
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(history) { res ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Tugas: ${res.assignment?.request?.title ?: "N/A"}", style = MaterialTheme.typography.titleSmall)
                                Text("Link: ${res.fileUrl}", color = Color.Blue)
                                Text("Catatan: ${res.notes ?: "-"}")
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedAssignment != null) {
        SubmitWorkDialog(
            assignment = selectedAssignment!!,
            onDismiss = { selectedAssignment = null },
            onSubmit = { url, notes ->
                viewModel.submitWork(selectedAssignment!!.assignmentId, url, notes)
                selectedAssignment = null
            }
        )
    }
}

@Composable
fun SubmitWorkDialog(assignment: Assignment, onDismiss: () -> Unit, onSubmit: (String, String) -> Unit) {
    var url by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Submit: ${assignment.request.title}") },
        text = {
            Column {
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Link Google Drive/File") })
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Catatan") })
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(url, notes) }, enabled = url.isNotBlank()) { Text("Submit") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}