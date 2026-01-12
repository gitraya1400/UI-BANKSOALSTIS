package com.example.stisbanksoal.ui.screens.dosen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.data.model.Soal
import com.example.stisbanksoal.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoalListScreen(
    pertemuanId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: SoalViewModel = viewModel(factory = factory)

    // Load data awal saat layar dibuka
    LaunchedEffect(pertemuanId) {
        viewModel.loadSoal(pertemuanId)
    }

    // State untuk Dialog Tambah Soal
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bank Soal") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Soal")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            // Loading Indicator
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Error Message
            viewModel.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }

            // State Kosong
            if (!viewModel.isLoading && viewModel.soalList.isEmpty()) {
                Text(
                    text = "Belum ada soal. Klik + untuk menambah.",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // List Soal
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.soalList) { soal ->
                    SoalCard(
                        soal = soal,
                        onDelete = { viewModel.deleteSoal(soal.id) }
                    )
                }
            }
        }

        // --- DIALOG FORM TAMBAH SOAL ---
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Buat Soal Baru") },
                text = {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth()
                    ) {
                        // 1. Pilihan Tipe Soal
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = viewModel.tipeSoalInput == "PILIHAN_GANDA",
                                onClick = { viewModel.tipeSoalInput = "PILIHAN_GANDA" }
                            )
                            Text("Pilihan Ganda", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.width(8.dp))
                            RadioButton(
                                selected = viewModel.tipeSoalInput == "ESAI",
                                onClick = { viewModel.tipeSoalInput = "ESAI" }
                            )
                            Text("Esai", style = MaterialTheme.typography.bodyMedium)
                        }

                        // 2. Input Pertanyaan
                        OutlinedTextField(
                            value = viewModel.pertanyaanInput,
                            onValueChange = { viewModel.pertanyaanInput = it },
                            label = { Text("Pertanyaan") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )

                        // 3. Tingkat Kesulitan
                        Spacer(Modifier.height(8.dp))
                        Text("Tingkat Kesulitan:", style = MaterialTheme.typography.labelMedium)
                        Row(modifier = Modifier.padding(top = 4.dp)) {
                            listOf("MUDAH", "SEDANG", "SULIT").forEach { level ->
                                FilterChip(
                                    selected = viewModel.tingkatKesulitanInput == level,
                                    onClick = { viewModel.tingkatKesulitanInput = level },
                                    label = { Text(level) },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }

                        // 4. Form Spesifik (PG / Esai)
                        Spacer(Modifier.height(8.dp))
                        if (viewModel.tipeSoalInput == "PILIHAN_GANDA") {
                            FormPilihanGanda(viewModel)
                        } else {
                            FormEsai(viewModel)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        // Validasi sederhana sebelum simpan
                        if (viewModel.pertanyaanInput.isBlank()) {
                            Toast.makeText(context, "Pertanyaan wajib diisi", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.createSoal {
                                showDialog = false
                                Toast.makeText(context, "Soal berhasil disimpan", Toast.LENGTH_SHORT).show()
                            }
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

// --- SUB-COMPONENTS ---

@Composable
fun FormPilihanGanda(viewModel: SoalViewModel) {
    Column {
        Text("Opsi Jawaban:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top=8.dp))
        OutlinedTextField(value = viewModel.opsiA, onValueChange = { viewModel.opsiA = it }, label = { Text("Opsi A") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = viewModel.opsiB, onValueChange = { viewModel.opsiB = it }, label = { Text("Opsi B") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = viewModel.opsiC, onValueChange = { viewModel.opsiC = it }, label = { Text("Opsi C") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = viewModel.opsiD, onValueChange = { viewModel.opsiD = it }, label = { Text("Opsi D") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = viewModel.opsiE, onValueChange = { viewModel.opsiE = it }, label = { Text("Opsi E") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(8.dp))
        Text("Kunci Jawaban (Pilih Index):", style = MaterialTheme.typography.labelMedium)
        // Dropdown atau Radio Button sederhana untuk kunci jawaban agar tidak salah input angka
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            listOf("A" to 0, "B" to 1, "C" to 2, "D" to 3, "E" to 4).forEach { (label, value) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RadioButton(
                        selected = viewModel.kunciJawabanPG == value,
                        onClick = { viewModel.kunciJawabanPG = value }
                    )
                    Text(label)
                }
            }
        }
    }
}

@Composable
fun FormEsai(viewModel: SoalViewModel) {
    Column {
        OutlinedTextField(
            value = viewModel.kunciJawabanEsai,
            onValueChange = { viewModel.kunciJawabanEsai = it },
            label = { Text("Kunci Jawaban Esai (Opsional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
    }
}

@Composable
fun SoalCard(soal: Soal, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Info Soal
                Column(modifier = Modifier.weight(1f)) {
                    // Badge Tipe & Kesulitan
                    Row {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = soal.tipeSoal,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = soal.tingkatKesulitan,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(text = soal.pertanyaan, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)

                    // Tampilkan Opsi jika PG
                    if (soal.tipeSoal == "PILIHAN_GANDA") {
                        Spacer(Modifier.height(8.dp))
                        soal.opsiJawaban.forEachIndexed { index, opsi ->
                            val label = ('A' + index).toString()
                            // Highlight kunci jawaban jika ada (opsional, bisa disembunyikan)
                            val isKey = index == soal.kunciJawabanIndex
                            Text(
                                text = "$label. $opsi ${if(isKey) "✅" else ""}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if(isKey) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Tombol Hapus
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}