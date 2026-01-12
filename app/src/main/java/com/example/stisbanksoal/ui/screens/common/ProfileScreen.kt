package com.example.stisbanksoal.ui.screens.common

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.ui.ViewModelFactory
import com.example.stisbanksoal.ui.theme.*

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateHome: () -> Unit // Callback balik ke Home
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: ProfileViewModel = viewModel(factory = factory) // Pakai ViewModel baru

    // Dialog States
    var showEditDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Gray50,
        bottomBar = {
            // NAVBAR TETAP ADA (Konsisten)
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateHome, // Balik ke Home
                    icon = { Icon(Icons.Default.Book, null) },
                    label = { Text("Beranda") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Blue900, indicatorColor = Blue50)
                )
                NavigationBarItem(
                    selected = true, // Sedang aktif
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profil") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Blue900, indicatorColor = Blue50)
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            // Background Loading jika data belum siap
            if (viewModel.isLoading && viewModel.user == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Blue900)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- HEADER PROFIL ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(
                                color = Blue900,
                                shape = RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Avatar Besar
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(Blue700, CircleShape)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(Yellow500),
                                contentAlignment = Alignment.Center
                            ) {
                                // Inisial dari Data Asli
                                Text(
                                    text = viewModel.user?.name?.take(2)?.uppercase() ?: "US",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Blue900
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Nama & Role Asli
                            Text(
                                text = viewModel.user?.name ?: "Memuat...",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = viewModel.user?.role?.uppercase() ?: "DOSEN",
                                color = Blue100,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- FORM INFO (Read Only) ---
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                    ) {
                        ProfileInfoCard("Nama Lengkap", viewModel.user?.name ?: "-", Icons.Default.Person)
                        Spacer(modifier = Modifier.height(16.dp))
                        // Asumsi model User punya field NIP/Email
                        ProfileInfoCard("Email", viewModel.user?.email ?: "-", Icons.Default.Email)
                        Spacer(modifier = Modifier.height(16.dp))
                        // Jika model User punya field nip, tampilkan. Jika tidak, hapus baris ini.
                        // ProfileInfoCard("NIP", viewModel.user?.nip ?: "-", Icons.Default.Badge)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // --- ACTION BUTTONS ---
                    Column(modifier = Modifier.padding(24.dp)) {
                        // Edit Profil Button
                        Button(
                            onClick = { showEditDialog = true }, // Munculkan Dialog
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Blue900),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Edit, null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit Profil")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Ganti Password Button
                        OutlinedButton(
                            onClick = { showPasswordDialog = true }, // Munculkan Dialog
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Lock, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ganti Password", color = Gray700)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Logout
                        Button(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Red500),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Logout", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // --- DIALOG EDIT PROFIL ---
            if (showEditDialog) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    title = { Text("Edit Profil") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = viewModel.editName,
                                onValueChange = { viewModel.editName = it },
                                label = { Text("Nama Lengkap") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = viewModel.editEmail,
                                onValueChange = { viewModel.editEmail = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.updateProfile {
                                showEditDialog = false
                                Toast.makeText(context, "Profil Diperbarui!", Toast.LENGTH_SHORT).show()
                            }
                        }) { Text("Simpan") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false }) { Text("Batal") }
                    },
                    containerColor = Color.White
                )
            }

            // --- DIALOG GANTI PASSWORD ---
            if (showPasswordDialog) {
                AlertDialog(
                    onDismissRequest = { showPasswordDialog = false },
                    title = { Text("Ganti Password") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = viewModel.oldPassword,
                                onValueChange = { viewModel.oldPassword = it },
                                label = { Text("Password Lama") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = viewModel.newPassword,
                                onValueChange = { viewModel.newPassword = it },
                                label = { Text("Password Baru") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.changePassword {
                                showPasswordDialog = false
                                Toast.makeText(context, "Password Berhasil Diganti!", Toast.LENGTH_SHORT).show()
                            }
                        }) { Text("Ganti") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPasswordDialog = false }) { Text("Batal") }
                    },
                    containerColor = Color.White
                )
            }
        }
    }
}

@Composable
fun ProfileInfoCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Gray500)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Blue900, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = Gray700)
            }
        }
    }
}