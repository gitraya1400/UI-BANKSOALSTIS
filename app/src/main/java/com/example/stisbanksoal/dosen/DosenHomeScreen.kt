package com.example.stisbanksoal.ui.screens.dosen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.data.model.MataKuliah
import com.example.stisbanksoal.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosenHomeScreen(
    onLogout: () -> Unit,
    onMataKuliahClick: (Long) -> Unit // <--- Parameter Baru
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: DosenViewModel = viewModel(factory = factory)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Dosen Dashboard", style = MaterialTheme.typography.titleMedium)
                        if(viewModel.dosenName.isNotEmpty()) {
                            Text("Hi, ${viewModel.dosenName}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
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

            if (!viewModel.isLoading && viewModel.mataKuliahList.isEmpty()) {
                Text(
                    text = "Belum ada Mata Kuliah yang ditugaskan.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.mataKuliahList) { mk ->
                    DosenMataKuliahCard(mk = mk) {
                        // Nanti navigasi ke Pertemuan di sini
                        android.widget.Toast.makeText(context, "Buka ${mk.nama}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun DosenMataKuliahCard(mk: MataKuliah, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = mk.nama, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(text = "${mk.kode} | Semester ${mk.semester}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}