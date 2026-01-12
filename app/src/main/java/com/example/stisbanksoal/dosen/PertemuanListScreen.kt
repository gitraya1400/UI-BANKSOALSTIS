package com.example.stisbanksoal.ui.screens.dosen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.data.model.Pertemuan
import com.example.stisbanksoal.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PertemuanListScreen(
    mataKuliahId: Long,
    onBack: () -> Unit,
    onPertemuanClick: (Long) -> Unit // Nanti navigasi ke Soal
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: PertemuanViewModel = viewModel(factory = factory)

    // Load data saat pertama kali dibuka
    LaunchedEffect(mataKuliahId) {
        viewModel.loadPertemuan(mataKuliahId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Pertemuan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            viewModel.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }

            if (!viewModel.isLoading && viewModel.pertemuanList.isEmpty()) {
                Text(
                    text = "Belum ada pertemuan di mata kuliah ini.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.pertemuanList) { p ->
                    PertemuanCard(pertemuan = p, onClick = { onPertemuanClick(p.id) })
                }
            }
        }
    }
}

@Composable
fun PertemuanCard(pertemuan: Pertemuan, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Pertemuan ke-${pertemuan.judul}", fontWeight = FontWeight.Bold) // Sesuaikan field judul/urutan
            Text(text = "Tanggal: ${pertemuan.tanggal}", style = MaterialTheme.typography.bodySmall)
        }
    }
}