package com.example.stisbanksoal.ui.screens.dosen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.ui.ViewModelFactory
import com.example.stisbanksoal.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PertemuanListScreen(
    mataKuliahId: Long,
    onBack: () -> Unit,
    onPertemuanClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: PertemuanViewModel = viewModel(factory = factory)

    LaunchedEffect(mataKuliahId) {
        viewModel.loadPertemuan(mataKuliahId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Pertemuan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Blue900
                )
            )
        },
        containerColor = Gray50
    ) { paddingValues ->

        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Pilih pertemuan untuk mengelola soal:", color = Gray500, fontSize = 14.sp, modifier = Modifier.padding(bottom=8.dp))
                }

                items(viewModel.pertemuanList) { pertemuan ->
                    PertemuanCard(
                        judul = "Pertemuan ${pertemuan.nomorPertemuan}",
                        deskripsi = pertemuan.judul, // Asumsi judul materi ada di field judul
                        onClick = { onPertemuanClick(pertemuan.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PertemuanCard(judul: String, deskripsi: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
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
                    .size(48.dp)
                    .background(Blue50, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = judul.filter { it.isDigit() }, // Ambil angka pertemuan
                    fontWeight = FontWeight.Bold,
                    color = Blue900,
                    fontSize = 18.sp
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(deskripsi, fontWeight = FontWeight.Bold, color = Gray900)
                Text(judul, fontSize = 12.sp, color = Gray500)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Gray400)
        }
    }
}