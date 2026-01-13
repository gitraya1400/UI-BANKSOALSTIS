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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun PertemuanListScreen(
    mataKuliahId: Long,
    onBack: () -> Unit,
    onNavigateHome: () -> Unit, // [BARU]
    onNavigateToProfile: () -> Unit, // [BARU]
    onPertemuanClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: PertemuanViewModel = viewModel(factory = factory)

    LaunchedEffect(mataKuliahId) {
        viewModel.loadPertemuan(mataKuliahId)
    }

    val headerBrush = Brush.verticalGradient(colors = listOf(Blue900, Blue700))

    Scaffold(
        containerColor = Gray50,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = true, // Masih dianggap bagian dari Beranda
                    onClick = onNavigateHome,
                    icon = { Icon(Icons.Default.Home, "Beranda") },
                    label = { Text("Beranda") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = { Icon(Icons.Default.Person, "Profil") },
                    label = { Text("Profil") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(headerBrush, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            ) {
                Row(
                    modifier = Modifier.padding(top = 40.dp, start = 16.dp, end = 16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.White)
                    }
                    Spacer(Modifier.width(16.dp))
                    Text("Daftar Pertemuan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }

            // List Pertemuan
            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Blue900)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.pertemuanList) { p ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { onPertemuanClick(p.id) }
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(color = Blue50, shape = CircleShape, modifier = Modifier.size(40.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("${p.nomorPertemuan}", fontWeight = FontWeight.Bold, color = Blue900)
                                    }
                                }
                                Spacer(Modifier.width(16.dp))
                                Text(p.judul, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}