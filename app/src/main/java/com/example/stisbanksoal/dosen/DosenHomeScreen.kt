package com.example.stisbanksoal.ui.screens.dosen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.ui.ViewModelFactory
import com.example.stisbanksoal.ui.theme.*

@Composable
fun DosenHomeScreen(
    onNavigateToPertemuan: (Long) -> Unit,
    onNavigateToProfile: () -> Unit, // [BARU] Tambahkan ini
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: DosenViewModel = viewModel(factory = factory)

    val headerBrush = Brush.verticalGradient(colors = listOf(Blue900, Blue700))

    // Bungkus dengan Scaffold agar ada Navbar
    Scaffold(
        containerColor = Gray50,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = true, // Selalu aktif karena ini Halaman Utama
                    onClick = { /* Sudah di sini */ },
                    icon = { Icon(Icons.Default.Home, "Beranda") },
                    label = { Text("Beranda") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile, // Pindah ke Profil
                    icon = { Icon(Icons.Default.Person, "Profil") },
                    label = { Text("Profil") }
                )
            }
        }
    ) { paddingValues ->
        // Gunakan paddingValues dari Scaffold
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(headerBrush, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = viewModel.dosenName.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Selamat Datang,", color = Blue100, fontSize = 14.sp)
                            Text(viewModel.dosenName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.Logout, "Logout", tint = Color.White)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("Mata Kuliah Anda", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            // LIST MATA KULIAH
            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Blue900)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.mataKuliahList) { mk ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { onNavigateToPertemuan(mk.id) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Blue50, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Book, null, tint = Blue900)
                                }
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(mk.nama, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Kode: ${mk.kode}", color = Gray500, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}