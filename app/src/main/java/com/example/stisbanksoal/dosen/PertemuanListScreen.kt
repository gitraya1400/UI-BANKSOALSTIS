package com.example.stisbanksoal.ui.screens.dosen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.ui.ViewModelFactory
import com.example.stisbanksoal.ui.theme.*

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
        containerColor = Gray50
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            // --- HEADER BACKGROUND ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        color = Blue900,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // --- TOP BAR ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Detail Pertemuan",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // --- INFO CARD ---
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .background(color = Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = "Mata Kuliah ID: $mataKuliahId", // Nanti bisa diganti Nama MK
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Blue900,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Daftar Materi",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${viewModel.pertemuanList.size} Pertemuan Tersedia",
                            color = Blue100,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- LIST MENU ---
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = "Menu",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gray700,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Render List Pertemuan
                    viewModel.pertemuanList.forEach { pertemuan ->
                        MeetingMenuCard(
                            // Ganti 'pertemuan.tanggal' dengan data baru
                            title = "Pertemuan ${pertemuan.nomorPertemuan}: ${pertemuan.judul}",
                            subtitle = if (pertemuan.deskripsi.isNotEmpty()) pertemuan.deskripsi else "Klik untuk kelola bank soal",
                            onClick = { onPertemuanClick(pertemuan.id) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (viewModel.pertemuanList.isEmpty()) {
                        Text(
                            text = "Belum ada pertemuan yang dibuat oleh Admin.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray500
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MeetingMenuCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Blue50, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = null,
                    tint = Blue900,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Gray700
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500,
                    maxLines = 2
                )
            }
        }
    }
}