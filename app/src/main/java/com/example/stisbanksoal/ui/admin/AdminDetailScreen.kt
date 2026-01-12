package com.example.stisbanksoal.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.data.model.Pertemuan
import com.example.stisbanksoal.data.model.User
import com.example.stisbanksoal.ui.ViewModelFactory
import com.example.stisbanksoal.ui.theme.*

@Composable
fun AdminDetailScreen(
    mkId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: AdminDetailViewModel = viewModel(factory = factory)

    LaunchedEffect(mkId) {
        viewModel.loadData(mkId)
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pertemuan", "Dosen Pengajar")

    // State Dialog
    var showPertemuanDialog by remember { mutableStateOf(false) }
    var showDosenDialog by remember { mutableStateOf(false) }

    // State Edit Pertemuan (FIX: Pastikan variabel ini ada)
    var isEditing by remember { mutableStateOf(false) }
    var editPertemuanId by remember { mutableLongStateOf(0L) }

    Scaffold(
        containerColor = Gray50,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) {
                        isEditing = false // Mode Tambah
                        viewModel.inputNomorPertemuan = ""
                        viewModel.inputJudulPertemuan = ""
                        viewModel.inputDeskripsi = ""
                        showPertemuanDialog = true
                    } else showDosenDialog = true
                },
                containerColor = Yellow500,
                contentColor = Blue900,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(if (selectedTab == 0) Icons.Default.Add else Icons.Default.PersonAdd, "Tambah")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier.fillMaxWidth().height(240.dp)
                    .background(Blue900, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            )

            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.background(Color.White.copy(0.2f), CircleShape)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Manajemen Mata Kuliah", color = Blue100, style = MaterialTheme.typography.bodySmall)
                        Text("Detail Akademik", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }

                // Tab Row
                Box(modifier = Modifier.padding(horizontal = 24.dp).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(0.1f))) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab]), color = Yellow500, height = 4.dp)
                        },
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal, color = if (selectedTab == index) Color.White else Blue100) }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Content
                Box(modifier = Modifier.fillMaxSize().background(Gray50)) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Blue900)
                    } else {
                        if (selectedTab == 0) {
                            PertemuanListContent(
                                pertemuanList = viewModel.pertemuanList,
                                onDelete = { viewModel.deletePertemuan(it) },
                                onEdit = { pertemuan ->
                                    // Buka Dialog Edit
                                    isEditing = true
                                    editPertemuanId = pertemuan.id
                                    viewModel.inputNomorPertemuan = pertemuan.nomorPertemuan.toString()
                                    viewModel.inputJudulPertemuan = pertemuan.judul
                                    viewModel.inputDeskripsi = pertemuan.deskripsi
                                    showPertemuanDialog = true
                                }
                            )
                        } else {
                            // FIX: Pastikan fungsi ini ada di bawah
                            DosenListContent(
                                assignedDosenList = viewModel.assignedDosenList,
                                onRemove = { viewModel.removeDosen(it) }
                            )
                        }
                    }
                }
            }
        }

        // --- DIALOG PERTEMUAN (Tambah & Edit) ---
        if (showPertemuanDialog) {
            AlertDialog(
                onDismissRequest = { showPertemuanDialog = false },
                title = { Text(if (isEditing) "Edit Pertemuan" else "Buat Pertemuan Baru", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = viewModel.inputNomorPertemuan,
                            onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.inputNomorPertemuan = it },
                            label = { Text("Nomor Pertemuan") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = { Icon(Icons.Default.FormatListNumbered, null) }
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = viewModel.inputJudulPertemuan,
                            onValueChange = { viewModel.inputJudulPertemuan = it },
                            label = { Text("Judul Pertemuan") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Title, null) }
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = viewModel.inputDeskripsi,
                            onValueChange = { viewModel.inputDeskripsi = it },
                            label = { Text("Deskripsi") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            leadingIcon = { Icon(Icons.Default.Description, null) }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (isEditing) {
                                // Logic Update (Sementara pakai create dulu jika belum ada update)
                                // Nanti update viewModel.updatePertemuan(editPertemuanId)
                                viewModel.createPertemuan { showPertemuanDialog = false }
                            } else {
                                viewModel.createPertemuan { showPertemuanDialog = false }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Blue900)
                    ) { Text("Simpan") }
                },
                dismissButton = {
                    TextButton(onClick = { showPertemuanDialog = false }) { Text("Batal", color = Gray500) }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
        }

        // --- DIALOG ASSIGN DOSEN ---
        if (showDosenDialog) {
            var searchQuery by remember { mutableStateOf("") }
            val filteredDosen = viewModel.allDosenList.filter {
                it.name.contains(searchQuery, ignoreCase = true) || it.email.contains(searchQuery, ignoreCase = true)
            }

            AlertDialog(
                onDismissRequest = { showDosenDialog = false },
                title = { Text("Pilih Dosen Pengajar") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Cari Dosen") },
                            leadingIcon = { Icon(Icons.Default.Search, null) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        LazyColumn(modifier = Modifier.height(300.dp)) {
                            items(filteredDosen) { dosen ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.assignDosen(dosen.id) {
                                                showDosenDialog = false
                                            }
                                        }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Person, null, tint = Blue900)
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(dosen.name, fontWeight = FontWeight.Bold)
                                        Text(dosen.email, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                HorizontalDivider()
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = { TextButton(onClick = { showDosenDialog = false }) { Text("Tutup") } },
                containerColor = Color.White
            )
        }
    }
}

// --- SUB-COMPONENTS UI ---

@Composable
fun PertemuanListContent(
    pertemuanList: List<Pertemuan>,
    onDelete: (Long) -> Unit,
    onEdit: (Pertemuan) -> Unit // Callback Edit
) {
    if (pertemuanList.isEmpty()) {
        EmptyState("Belum ada pertemuan dibuat.")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(pertemuanList) { p ->
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(48.dp).background(Blue50, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Description, null, tint = Blue900)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Pertemuan ${p.nomorPertemuan}", fontWeight = FontWeight.Bold, color = Blue700, fontSize = 14.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(p.judul, fontWeight = FontWeight.Bold, color = Gray700, fontSize = 16.sp)
                            if (p.deskripsi.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                Text(p.deskripsi, style = MaterialTheme.typography.bodySmall, color = Gray500, maxLines = 2)
                            }
                        }
                        // Tombol Edit (Pensil)
                        IconButton(onClick = { onEdit(p) }) {
                            Icon(Icons.Default.Edit, null, tint = Blue700)
                        }
                        // Tombol Delete (Sampah)
                        IconButton(onClick = { onDelete(p.id) }) {
                            Icon(Icons.Default.Delete, null, tint = Red500)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DosenListContent(
    assignedDosenList: List<User>,
    onRemove: (Long) -> Unit
) {
    if (assignedDosenList.isEmpty()) {
        EmptyState("Belum ada dosen ditugaskan.")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Pengajar Terdaftar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Gray700
                )
            }
            items(assignedDosenList) { dosen ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Blue50, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(dosen.name.take(2).uppercase(), fontWeight = FontWeight.Bold, color = Blue900)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(dosen.name, fontWeight = FontWeight.Bold, color = Gray700)
                            Text(dosen.email, style = MaterialTheme.typography.bodySmall, color = Gray500)
                        }
                        IconButton(onClick = { onRemove(dosen.id) }) {
                            Icon(Icons.Default.Delete, null, tint = Red500)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Description, null, modifier = Modifier.size(64.dp), tint = Blue100)
        Spacer(Modifier.height(16.dp))
        Text(message, style = MaterialTheme.typography.titleMedium, color = Gray500)
    }
}