package com.example.stisbanksoal.ui.screens.dosen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.data.model.Soal
import com.example.stisbanksoal.ui.ViewModelFactory
import com.example.stisbanksoal.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoalListScreen(
    pertemuanId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: SoalViewModel = viewModel(factory = factory)

    LaunchedEffect(pertemuanId) { viewModel.loadSoal(pertemuanId) }

    // State untuk Dialog Form (Tambah/Edit)
    var showFormDialog by remember { mutableStateOf(false) }

    // State untuk Dialog Detail
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedSoal by remember { mutableStateOf<Soal?>(null) }

    val headerBrush = Brush.verticalGradient(colors = listOf(Blue900, Blue700))

    Scaffold(
        containerColor = Gray50,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.resetForm() // Pastikan form bersih untuk tambah baru
                    showFormDialog = true
                },
                containerColor = Blue900,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Tambah Soal")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            Column {
                // --- CUSTOM HEADER ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(headerBrush, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.White)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Bank Soal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("Kelola pertanyaan pertemuan ini", color = Blue100, fontSize = 14.sp)
                        }
                    }
                }

                // --- LIST ---
                Box(modifier = Modifier.fillMaxSize()) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (viewModel.soalList.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.FolderOpen, null, tint = Gray400, modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Belum ada soal", color = Gray500)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(viewModel.soalList) { soal ->
                                SoalItemCard(
                                    soal = soal,
                                    onDelete = { viewModel.deleteSoal(soal.id) },
                                    onEdit = {
                                        viewModel.prepareEdit(soal)
                                        showFormDialog = true
                                    },
                                    onClick = {
                                        selectedSoal = soal
                                        showDetailDialog = true
                                    }
                                )
                            }
                            item { Spacer(Modifier.height(80.dp)) }
                        }
                    }
                }
            }

            // Error Overlay
            if (viewModel.errorMessage != null) {
                Surface(color = Red500, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopCenter)) {
                    Text(text = viewModel.errorMessage ?: "", color = Color.White, modifier = Modifier.padding(12.dp))
                }
            }
        }

        // --- DIALOG FORM (CREATE / EDIT) ---
        if (showFormDialog) {
            AddSoalDialog(
                viewModel = viewModel,
                onDismiss = { showFormDialog = false },
                onSave = {
                    viewModel.saveSoal {
                        showFormDialog = false
                        Toast.makeText(context, "Berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // --- DIALOG DETAIL ---
        if (showDetailDialog && selectedSoal != null) {
            DetailSoalDialog(
                soal = selectedSoal!!,
                onDismiss = { showDetailDialog = false }
            )
        }
    }
}

@Composable
fun DetailSoalDialog(soal: Soal, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detail Soal", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row {
                    SuggestionChip(onClick = {}, label = { Text(soal.tipeSoal) })
                    Spacer(Modifier.width(8.dp))
                    SuggestionChip(onClick = {}, label = { Text(soal.tingkatKesulitan) })
                }
                Spacer(Modifier.height(8.dp))
                Text("Pertanyaan:", fontWeight = FontWeight.Bold)
                Text(soal.pertanyaan)

                Spacer(Modifier.height(16.dp))

                if (soal.tipeSoal == "PILIHAN_GANDA") {
                    Text("Opsi Jawaban:", fontWeight = FontWeight.Bold)

                    // PERBAIKAN: Gunakan let dan pastikan data tidak null sebelum loop
                    val opsis = soal.opsiJawaban ?: emptyList() //

                    if (opsis.isEmpty()) {
                        Text("- Tidak ada data opsi -", color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    } else {
                        opsis.forEachIndexed { i, opsi ->
                            val isKey = i == (soal.kunciJawabanIndex ?: -1)
                            Row(Modifier.padding(vertical = 4.dp)) {
                                Text("${('A' + i)}.", fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                                Text(
                                    text = "$opsi ${if(isKey) "✅" else ""}",
                                    fontWeight = if(isKey) FontWeight.Bold else FontWeight.Normal,
                                    color = if(isKey) Green700 else Color.Unspecified
                                )
                            }
                        }
                    }
                } else {
                    Text("Kunci Jawaban:", fontWeight = FontWeight.Bold)
                    Text(soal.kunciJawabanEsai ?: "-")
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Tutup") } }
    )
}

@Composable
fun AddSoalDialog(viewModel: SoalViewModel, onDismiss: () -> Unit, onSave: () -> Unit) {
    val title = if(viewModel.isEditMode) "Edit Soal" else "Buat Soal Baru"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    listOf("PILIHAN_GANDA", "ESAI").forEach { type ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end=12.dp)) {
                            // Disable ganti tipe saat edit agar tidak error
                            RadioButton(
                                selected = viewModel.tipeSoalInput == type,
                                onClick = { if(!viewModel.isEditMode) viewModel.tipeSoalInput = type },
                                enabled = !viewModel.isEditMode
                            )
                            Text(if (type == "PILIHAN_GANDA") "PG" else "Esai", fontSize = 14.sp)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.pertanyaanInput,
                    onValueChange = { viewModel.pertanyaanInput = it },
                    label = { Text("Pertanyaan") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(Modifier.height(8.dp))
                Text("Kesulitan:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                Spacer(Modifier.height(16.dp))
                if (viewModel.tipeSoalInput == "PILIHAN_GANDA") FormPGDynamic(viewModel)
                else FormEsai(viewModel)
            }
        },
        confirmButton = { Button(onClick = onSave, colors = ButtonDefaults.buttonColors(containerColor = Blue900)) { Text("Simpan") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun FormPGDynamic(viewModel: SoalViewModel) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Opsi Jawaban:", fontWeight = FontWeight.Bold)
            TextButton(onClick = { viewModel.addOpsi() }) { Text("+ Tambah") }
        }
        viewModel.opsiJawabanList.forEachIndexed { index, text ->
            val label = ('A' + index).toString()
            val isKey = viewModel.kunciJawabanPG == index
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                RadioButton(selected = isKey, onClick = { viewModel.kunciJawabanPG = index })
                OutlinedTextField(
                    value = text,
                    onValueChange = { viewModel.updateOpsi(index, it) },
                    label = { Text("Opsi $label") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = if(isKey) Green500.copy(0.1f) else Color.Transparent)
                )
                if (viewModel.opsiJawabanList.size > 2) {
                    IconButton(onClick = { viewModel.removeOpsi(index) }) { Icon(Icons.Default.Close, null, tint = Gray400) }
                }
            }
        }
    }
}

@Composable
fun FormEsai(viewModel: SoalViewModel) {
    OutlinedTextField(
        value = viewModel.kunciJawabanEsai,
        onValueChange = { viewModel.kunciJawabanEsai = it },
        label = { Text("Kunci Jawaban") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SoalItemCard(soal: Soal, onDelete: () -> Unit, onEdit: () -> Unit, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } //
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Surface(color = Blue50, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = if (soal.tipeSoal == "PILIHAN_GANDA") "PG (${soal.tingkatKesulitan})" else "ESAI (${soal.tingkatKesulitan})",
                        color = Blue900, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Edit, null, tint = Blue700) }
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Delete, null, tint = Red500) }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(soal.pertanyaan, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(8.dp))
            Text("Klik untuk melihat detail...", fontSize = 12.sp, color = Gray400)
        }
    }
}