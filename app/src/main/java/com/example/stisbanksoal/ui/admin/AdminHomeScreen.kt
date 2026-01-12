package com.example.stisbanksoal.ui.screens.admin

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.data.model.MataKuliah
import com.example.stisbanksoal.ui.ViewModelFactory
import com.example.stisbanksoal.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onNavigateToProfile: () -> Unit,
    onMataKuliahClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: AdminViewModel = viewModel(factory = factory)

    var showAddDialog by remember { mutableStateOf(false) }

    // Header Gradient
    val headerBrush = Brush.horizontalGradient(
        colors = listOf(Blue900, Blue700, Color(0xFFD97706))
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Blue50,
                contentColor = Blue900,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "Tambah MK")
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Book, null) },
                    label = { Text("Beranda") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Blue900, indicatorColor = Blue50)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profil") }
                )
            }
        },
        containerColor = Gray50
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            // --- HEADER BACKGROUND ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp) // Sedikit ditinggikan agar konten muat
                    .background(
                        brush = headerBrush,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // --- HEADER CONTENT (Rapih & Dinamis) ---
                Row(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp) // Padding aman dari status bar
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Admin Dashboard",
                            color = Yellow500,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = viewModel.userName, // DATA DINAMIS
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1 // Agar tidak turun baris jika nama panjang
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Avatar Dinamis
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Yellow500, CircleShape)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = viewModel.userInitial, // INISIAL DINAMIS
                            color = Blue900,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Judul Besar
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = "Bank Soal STIS",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kelola semua mata kuliah",
                        color = Blue100,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // --- LIST CARD ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daftar Mata Kuliah", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Gray700)
                    Text("${viewModel.mataKuliahList.size} Matkul", style = MaterialTheme.typography.bodySmall, color = Gray500)
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewModel.mataKuliahList) { mk ->
                        MataKuliahCardModern(
                            mk = mk,
                            onClick = { onMataKuliahClick(mk.id) },
                            onDelete = { viewModel.deleteMataKuliah(mk.id) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }

        // --- DIALOG (Tetap Ada) ---
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Tambah Mata Kuliah") },
                text = {
                    Column {
                        OutlinedTextField(value = viewModel.inputKode, onValueChange = { viewModel.inputKode = it }, label = { Text("Kode MK") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = viewModel.inputNama, onValueChange = { viewModel.inputNama = it }, label = { Text("Nama MK") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = viewModel.inputSks, onValueChange = { viewModel.inputSks = it }, label = { Text("SKS") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                            )
                            OutlinedTextField(value = viewModel.inputSemester, onValueChange = { viewModel.inputSemester = it }, label = { Text("Semester") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        }
                    }
                },
                confirmButton = { Button(onClick = { viewModel.createMataKuliah { showAddDialog = false } }, colors = ButtonDefaults.buttonColors(containerColor = Blue900)) { Text("Simpan") } },
                dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Batal") } },
                containerColor = Color.White
            )
        }
    }
}

// ... (MataKuliahCardModern tetap sama)
@Composable
fun MataKuliahCardModern(mk: MataKuliah, onClick: () -> Unit, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.size(48.dp).background(Blue50, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Book, null, tint = Blue900)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Surface(color = Blue50, shape = RoundedCornerShape(8.dp)) {
                        Text(mk.kode, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Blue900)
                    }
                    Box {
                        IconButton(onClick = { expanded = true }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Settings, null, tint = Gray400, modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(text = { Text("Assign Dosen") }, onClick = { expanded = false; onClick() }, leadingIcon = { Icon(Icons.Default.Person, null) })
                            DropdownMenuItem(text = { Text("Hapus", color = Red500) }, onClick = { expanded = false; onDelete() }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = Red500) })
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(mk.nama, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Gray700)
                Spacer(Modifier.height(4.dp))
                Text("Semester ${mk.semester} • ${mk.sks} SKS", style = MaterialTheme.typography.bodySmall, color = Gray500)
            }
        }
    }
}