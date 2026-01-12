package com.example.stisbanksoal.ui.screens.dosen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
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
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoalListScreen(
    pertemuanId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: SoalViewModel = viewModel(factory = factory)

    // Load data awal
    LaunchedEffect(pertemuanId) {
        viewModel.loadSoal(pertemuanId)
    }

    // State untuk Dialog
    var showDialog by remember { mutableStateOf(false) }

    // Launcher untuk ambil gambar dari Galeri
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Proses upload gambar (kita butuh file path nyata)
            val file = uriToFile(context, it)
            // Asumsi: Kita upload ke soal terakhir yang diedit atau logic lain
            // Untuk simplifikasi tugas ini, kita hanya toast dulu
            Toast.makeText(context, "Gambar siap diupload: ${file.name}", Toast.LENGTH_SHORT).show()
            // viewModel.uploadGambar(soalId, file) { ... }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bank Soal") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
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

            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Tampilkan error jika ada
            viewModel.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }

            // List Soal
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.soalList) { soal ->
                    SoalCard(
                        soal = soal,
                        onDelete = { viewModel.deleteSoal(soal.id) },
                        onUploadImage = {
                            // Panggil launcher galeri saat ikon gambar diklik
                            imagePickerLauncher.launch("image/*")
                        }
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
                    // Konten Dialog (Scrollable)
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth()
                    ) {
                        // 1. Pilihan Tipe Soal (Radio Button)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = viewModel.tipeSoalInput == "PILIHAN_GANDA",
                                onClick = { viewModel.tipeSoalInput = "PILIHAN_GANDA" }
                            )
                            Text("Pilihan Ganda")
                            Spacer(Modifier.width(8.dp))
                            RadioButton(
                                selected = viewModel.tipeSoalInput == "ESAI",
                                onClick = { viewModel.tipeSoalInput = "ESAI" }
                            )
                            Text("Esai")
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
                        Text("Tingkat Kesulitan: ${viewModel.tingkatKesulitanInput}")
                        Row {
                            listOf("MUDAH", "SEDANG", "SULIT").forEach { level ->
                                FilterChip(
                                    selected = viewModel.tingkatKesulitanInput == level,
                                    onClick = { viewModel.tingkatKesulitanInput = level },
                                    label = { Text(level) },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }

                        // 4. Form Spesifik
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
                        viewModel.createSoal {
                            showDialog = false
                            Toast.makeText(context, "Soal berhasil disimpan", Toast.LENGTH_SHORT).show()
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

// --- KOMPONEN UI TAMBAHAN ---

@Composable
fun FormPilihanGanda(viewModel: SoalViewModel) {
    Column {
        Text("Opsi Jawaban:", fontWeight = FontWeight.Bold)
        OutlinedTextField(value = viewModel.opsiA, onValueChange = { viewModel.opsiA = it }, label = { Text("Opsi A") })
        OutlinedTextField(value = viewModel.opsiB, onValueChange = { viewModel.opsiB = it }, label = { Text("Opsi B") })
        OutlinedTextField(value = viewModel.opsiC, onValueChange = { viewModel.opsiC = it }, label = { Text("Opsi C") })
        OutlinedTextField(value = viewModel.opsiD, onValueChange = { viewModel.opsiD = it }, label = { Text("Opsi D") })
        OutlinedTextField(value = viewModel.opsiE, onValueChange = { viewModel.opsiE = it }, label = { Text("Opsi E") })

        Spacer(Modifier.height(8.dp))
        Text("Kunci Jawaban (Index 0-4):")
        // Simplifikasi: Input angka 0-4 (0=A, 1=B, ...)
        OutlinedTextField(
            value = viewModel.kunciJawabanPG.toString(),
            onValueChange = { viewModel.kunciJawabanPG = it.toIntOrNull() ?: 0 },
            label = { Text("0=A, 1=B, 2=C...") }
        )
    }
}

@Composable
fun FormEsai(viewModel: SoalViewModel) {
    Column {
        OutlinedTextField(
            value = viewModel.kunciJawabanEsai,
            onValueChange = { viewModel.kunciJawabanEsai = it },
            label = { Text("Kunci Jawaban Esai") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
    }
}

@Composable
fun SoalCard(soal: Soal, onDelete: () -> Unit, onUploadImage: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Info Soal
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "[${soal.tipeSoal}] ${soal.tingkatKesulitan}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(text = soal.pertanyaan, fontWeight = FontWeight.Bold)

                    if (soal.gambar != null) {
                        Text("📷 Ada Gambar", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                        // Nanti bisa pakai COIL library untuk load gambar:
                        // AsyncImage(model = "http://.../files/${soal.gambar}", ...)
                    }
                }

                // Tombol Aksi
                Row {
                    IconButton(onClick = onUploadImage) {
                        Icon(Icons.Default.Image, contentDescription = "Upload Gambar")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

// Fungsi Bantuan: Mengubah URI Galeri menjadi File asli (untuk Upload)
fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
    val outputStream = FileOutputStream(tempFile)
    inputStream?.copyTo(outputStream)
    inputStream?.close()
    outputStream.close()
    return tempFile
}