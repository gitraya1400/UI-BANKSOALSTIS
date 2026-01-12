package com.example.stisbanksoal.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.R
import com.example.stisbanksoal.ui.ViewModelFactory
import com.example.stisbanksoal.ui.theme.*

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: AuthViewModel = viewModel(factory = factory)

    LaunchedEffect(viewModel.registerSuccess) {
        if (viewModel.registerSuccess) {
            Toast.makeText(context, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_LONG).show()
            onRegisterSuccess()
        }
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Blue900, Blue700, Yellow600)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Dekorasi Lingkaran
        Box(
            modifier = Modifier
                .offset(x = 40.dp, y = 80.dp)
                .size(128.dp)
                .alpha(0.2f)
                .background(Yellow400, CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Back
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackToLogin) {
                    // FIX 1: Gunakan AutoMirrored
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kembali ke Login", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- LOGO KECIL ---
            Image(
                painter = painterResource(id = R.drawable.logo_stis),
                contentDescription = "Logo STIS",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Buat Akun Baru",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Registrasi sebagai Dosen STIS",
                style = MaterialTheme.typography.bodyMedium,
                color = Yellow100,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Lengkapi data untuk membuat akun",
                        fontSize = 14.sp,
                        color = Gray500,
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    // Input Fields
                    RegisterField("Nama Lengkap", viewModel.regName, { viewModel.regName = it }, "Dr. Ahmad", Icons.Default.Person)
                    Spacer(Modifier.height(16.dp))
                    RegisterField("NIP", viewModel.regNip, { viewModel.regNip = it }, "198...", Icons.Default.Badge, keyboardType = KeyboardType.Number, helperText = "18 digit")
                    Spacer(Modifier.height(16.dp))
                    RegisterField("Email", viewModel.regEmail, { viewModel.regEmail = it }, "email@stis.ac.id", Icons.Default.Email, keyboardType = KeyboardType.Email)
                    Spacer(Modifier.height(16.dp))
                    RegisterField("Password", viewModel.regPassword, { viewModel.regPassword = it }, "••••", Icons.Default.Lock, isPassword = true)

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.register { } },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(
                                brush = Brush.horizontalGradient(listOf(Blue900, Blue700, Yellow600)),
                                shape = RoundedCornerShape(12.dp)
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (viewModel.isLoading) CircularProgressIndicator(color = Color.White)
                            else Text("Daftar Sekarang", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // FIX 2: Gunakan HorizontalDivider
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Gray200)
                        Text("ATAU", modifier = Modifier.padding(horizontal = 12.dp), fontSize = 12.sp, color = Gray500)
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Gray200)
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text("Sudah punya akun? ", color = Gray500)
                        Text("Masuk", color = Blue900, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onBackToLogin() })
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun RegisterField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    helperText: String? = null
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Gray700)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Gray400) },
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(icon, null, tint = Gray400) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Gray200,
                focusedBorderColor = Blue700,
                focusedContainerColor = Gray50
            ),
            singleLine = true
        )
        if (helperText != null) {
            Text(helperText, style = MaterialTheme.typography.bodySmall, color = Gray500, modifier = Modifier.padding(top = 4.dp, start = 4.dp))
        }
    }
}