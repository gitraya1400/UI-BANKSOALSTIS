package com.example.stisbanksoal.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.data.model.MataKuliah
import com.example.stisbanksoal.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onLogout: () -> Unit,
    onMataKuliahClick: (Long) -> Unit // <--- TAMBAHKAN PARAMETER INI
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: AdminViewModel = viewModel(factory = factory)

    // State untuk Dialog Tambah MK
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah MK")
            }
        }
    ) { paddingValues ->

        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            viewModel.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.mataKuliahList) { mk ->
                    MataKuliahCard(
                        mk = mk,
                        onDelete = { viewModel.deleteMataKuliah(mk.id) },
                        onClick = {
                            Toast.makeText(context, "Klik MK: ${mk.nama}", Toast.LENGTH_SHORT).show()
                            onMataKuliahClick(mk.id)                        }
                    )
                }
            }
        }

        // --- DIALOG TAMBAH MK ---
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Tambah Mata Kuliah") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = viewModel.inputKode,
                            onValueChange = { viewModel.inputKode = it },
                            label = { Text("Kode MK") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = viewModel.inputNama,
                            onValueChange = { viewModel.inputNama = it },
                            label = { Text("Nama MK") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            OutlinedTextField(
                                value = viewModel.inputSks,
                                onValueChange = { viewModel.inputSks = it },
                                label = { Text("SKS") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = viewModel.inputSemester,
                                onValueChange = { viewModel.inputSemester = it },
                                label = { Text("Semester") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = viewModel.inputDeskripsi,
                            onValueChange = { viewModel.inputDeskripsi = it },
                            label = { Text("Deskripsi") },
                            maxLines = 3
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.createMataKuliah {
                            showDialog = false
                            Toast.makeText(context, "Mata Kuliah Berhasil Dibuat", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Simpan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Batal") }
                }
            )
        }
    }
}

@Composable
fun MataKuliahCard(
    mk: MataKuliah,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = mk.nama, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = "${mk.kode} | Semester ${mk.semester} | ${mk.sks} SKS", style = MaterialTheme.typography.bodyMedium)
                if (mk.deskripsi.isNotEmpty()) {
                    Text(text = mk.deskripsi, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}