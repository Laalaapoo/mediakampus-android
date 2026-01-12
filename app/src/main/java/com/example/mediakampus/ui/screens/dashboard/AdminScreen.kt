package com.example.mediakampus.ui.screens.dashboard

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediakampus.data.model.Request
import com.example.mediakampus.data.model.UserResponseDto
import com.example.mediakampus.ui.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onLogout: () -> Unit,
    viewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val requests by viewModel.requests.collectAsState()
    val members by viewModel.members.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedReqForStatus by remember { mutableStateOf<Request?>(null) }
    var selectedReqForAssign by remember { mutableStateOf<Request?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadData()
        viewModel.loadAllUsers()
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Dashboard Admin") }, actions = {
                    Button(onClick = onLogout) { Text("Logout") }
                })
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Requests") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Manajemen User") })
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            if (selectedTab == 0) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(requests) { req ->
                        AdminRequestItem(
                            req = req,
                            onStatusClick = { selectedReqForStatus = req },
                            onAssignClick = { selectedReqForAssign = req }
                        )
                    }
                }
            } else {
                UserManagementContent(
                    users = allUsers,
                    onCreateUser = { n, e, p, r -> viewModel.createUser(n, e, p, r) },
                    onToggleStatus = { uid, stat -> viewModel.toggleUserActive(uid, stat) }
                )
            }
        }
    }

    // Dialog Status
    if (selectedReqForStatus != null) {
        StatusDialog(
            req = selectedReqForStatus!!,
            onDismiss = { selectedReqForStatus = null },
            onSubmit = { status, reason ->
                viewModel.updateStatus(selectedReqForStatus!!.requestId, status, reason)
                selectedReqForStatus = null
            }
        )
    }

    // Dialog Assign
    if (selectedReqForAssign != null) {
        AssignDialog(
            req = selectedReqForAssign!!,
            members = members,
            onDismiss = { selectedReqForAssign = null },
            onSubmit = { memberId ->
                viewModel.assignMember(selectedReqForAssign!!.requestId, memberId)
                selectedReqForAssign = null
            }
        )
    }
}

@Composable
fun AdminRequestItem(req: Request, onStatusClick: () -> Unit, onAssignClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${req.title} (${req.pemohon.name})", style = MaterialTheme.typography.titleMedium)
            Text("Waktu: ${req.eventTime}")
            Text("Status: ${req.status}", color = Color.Blue)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(onClick = onStatusClick, modifier = Modifier.padding(end=4.dp)) { Text("Status") }
                Button(onClick = onAssignClick) { Text("Assign") }
            }
        }
    }
}

@Composable
fun UserManagementContent(
    users: List<UserResponseDto>,
    onCreateUser: (String, String, String, String) -> Unit,
    onToggleStatus: (Long, Boolean) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Tambah User Baru")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(users) { user ->
                Card(modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.name, style = MaterialTheme.typography.titleSmall)
                            Text("${user.email} (${user.role})", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(checked = user.active, onCheckedChange = { onToggleStatus(user.id, user.active) })
                    }
                }
            }
        }
    }

    if (showDialog) {
        CreateUserDialog(
            onDismiss = { showDialog = false },
            onSubmit = onCreateUser
        )
    }
}

@Composable
fun CreateUserDialog(onDismiss: () -> Unit, onSubmit: (String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("ANGGOTA") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah User") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = role == "ANGGOTA", onClick = { role = "ANGGOTA" })
                    Text("Anggota")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(selected = role == "ADMIN", onClick = { role = "ADMIN" })
                    Text("Admin")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(name, email, password, role); onDismiss() }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun StatusDialog(req: Request, onDismiss: () -> Unit, onSubmit: (String, String?) -> Unit) {
    val statuses = listOf("PENDING", "DITERIMA", "DITOLAK", "DALAM_PROSES", "SELESAI")
    var selectedStatus by remember { mutableStateOf(req.status) }
    var reason by remember { mutableStateOf(req.rejectionReason ?: "") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Status") },
        text = {
            Column {
                Box {
                    OutlinedButton(onClick = { expanded = true }) { Text(selectedStatus) }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        statuses.forEach { s ->
                            DropdownMenuItem(text = { Text(s) }, onClick = { selectedStatus = s; expanded = false })
                        }
                    }
                }
                if (selectedStatus == "DITOLAK") {
                    OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("Alasan Penolakan") })
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(selectedStatus, if(selectedStatus=="DITOLAK") reason else null) }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun AssignDialog(req: Request, members: List<UserResponseDto>, onDismiss: () -> Unit, onSubmit: (Long) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf<UserResponseDto?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tugaskan Anggota") },
        text = {
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(selectedMember?.name ?: "Pilih Anggota")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    members.forEach { m ->
                        DropdownMenuItem(text = { Text(m.name) }, onClick = { selectedMember = m; expanded = false })
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { selectedMember?.let { onSubmit(it.id) } }, enabled = selectedMember != null) { Text("Assign") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}