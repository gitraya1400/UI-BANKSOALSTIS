package com.example.stisbanksoal.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDetailScreen(
    mkId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: AdminDetailViewModel = viewModel(factory = factory)

    // Load data pas pertama buka
    LaunchedEffect(mkId) {
        viewModel.loadData(mkId)
    }

    var showPertemuanDialog by remember { mutableStateOf(false) }
    var showDosenDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Mata Kuliah") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            // Tombol tambah beda fungsi tergantung tab (tapi kita simplifikasi: Tambah Pertemuan saja dulu)
            FloatingActionButton(onClick = { showPertemuanDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Pertemuan")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Tombol Assign Dosen
                Button(
                    onClick = { showDosenDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Assign Dosen Pengajar")
                }

                Spacer(Modifier.height(16.dp))
                Text("Daftar Pertemuan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(viewModel.pertemuanList) { p ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Topik: ${p.judul}", fontWeight = FontWeight.Bold)
                                    Text("Tanggal: ${p.tanggal}", style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { viewModel.deletePertemuan(p.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- DIALOG TAMBAH PERTEMUAN ---
        if (showPertemuanDialog) {
            AlertDialog(
                onDismissRequest = { showPertemuanDialog = false },
                title = { Text("Tambah Pertemuan") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = viewModel.inputJudulPertemuan,
                            onValueChange = { viewModel.inputJudulPertemuan = it },
                            label = { Text("Judul / Topik") }
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = viewModel.inputTanggalPertemuan,
                            onValueChange = { viewModel.inputTanggalPertemuan = it },
                            label = { Text("Tanggal (YYYY-MM-DD)") },
                            placeholder = { Text("2024-12-31") }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.createPertemuan { showPertemuanDialog = false }
                    }) { Text("Simpan") }
                },
                dismissButton = { TextButton(onClick = { showPertemuanDialog = false }) { Text("Batal") } }
            )
        }

        // --- DIALOG ASSIGN DOSEN ---
        if (showDosenDialog) {
            AlertDialog(
                onDismissRequest = { showDosenDialog = false },
                title = { Text("Pilih Dosen") },
                text = {
                    LazyColumn(modifier = Modifier.height(300.dp)) {
                        items(viewModel.allDosenList) { dosen ->
                            Text(
                                text = "${dosen.name} (${dosen.email})",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.assignDosen(dosen.id) {
                                            showDosenDialog = false
                                            Toast.makeText(context, "Dosen berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .padding(12.dp)
                            )
                            Divider()
                        }
                    }
                },
                confirmButton = {},
                dismissButton = { TextButton(onClick = { showDosenDialog = false }) { Text("Tutup") } }
            )
        }
    }
}