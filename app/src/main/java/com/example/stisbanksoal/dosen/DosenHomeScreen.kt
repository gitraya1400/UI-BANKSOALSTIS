package com.example.stisbanksoal.ui.screens.dosen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.data.model.MataKuliah
import com.example.stisbanksoal.ui.ViewModelFactory
import com.example.stisbanksoal.ui.theme.*

@Composable
fun DosenHomeScreen(
    onLogout: () -> Unit,
    onMataKuliahClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: DosenViewModel = viewModel(factory = factory)

    // Warna Gradient
    val headerBrush = Brush.verticalGradient(
        colors = listOf(Blue900, Blue700)
    )

    Scaffold(
        containerColor = Gray50
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            // --- HEADER ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(headerBrush, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Selamat Datang,", color = Blue100, fontSize = 14.sp)
                        Text(
                            text = if (viewModel.dosenName.isNotEmpty()) viewModel.dosenName else "Dosen",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.background(Color.White.copy(alpha=0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.Logout, null, tint = Color.White)
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text("Dashboard Pengajaran", color = Yellow500, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Text("Kelola bank soal mata kuliah Anda", color = Color.White, fontSize = 14.sp)
            }

            // --- CONTENT LIST ---
            Column(modifier = Modifier.padding(paddingValues).padding(top = 180.dp)) {

                if (viewModel.isLoading) {
                    Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Blue900)
                    }
                } else if (viewModel.mataKuliahList.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada mata kuliah.", color = Gray500)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(viewModel.mataKuliahList) { mk ->
                            MataKuliahCard(mk) { onMataKuliahClick(mk.id) }
                        }
                        item { Spacer(Modifier.height(40.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun MataKuliahCard(mk: MataKuliah, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Blue50, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Book, null, tint = Blue900, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(mk.nama, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Gray900)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Yellow100, shape = RoundedCornerShape(4.dp)) {
                        Text(mk.kode, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Yellow900, modifier = Modifier.padding(horizontal=4.dp, vertical=2.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Semester ${mk.semester} • ${mk.sks} SKS", fontSize = 12.sp, color = Gray500)
                }
            }
        }
    }
}