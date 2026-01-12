package com.example.stisbanksoal.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.ui.ViewModelFactory
import com.example.stisbanksoal.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: AuthViewModel = viewModel(factory = factory)

    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.loginError) {
        viewModel.loginError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    // Gradient Background (Blue900 -> Blue700 -> Yellow600)
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Blue900, Blue700, Yellow600)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Decorative Elements (Lingkaran-lingkaran background)
        Box(
            modifier = Modifier
                .offset(x = 40.dp, y = 80.dp)
                .size(128.dp)
                .alpha(0.2f)
                .background(Yellow400, CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-60).dp, y = (-120).dp)
                .size(160.dp)
                .alpha(0.2f)
                .background(Blue400, CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // --- LOGO SECTION ---
            Box(
                modifier = Modifier
                    .size(120.dp) // Ukuran Container (bisa diperbesar)
                    .shadow(16.dp, CircleShape)
                    .background(
                        brush = Brush.linearGradient(listOf(Yellow400, Yellow500)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.stisbanksoal.R.drawable.logo_stis), // <-- Sesuaikan nama file kamu
                    contentDescription = "Logo STIS",
                    modifier = Modifier
                        .size(180.dp) // Ukuran Gambar Logo
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Bank Soal STIS",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Sistem Manajemen Soal Ujian Akademik",
                style = MaterialTheme.typography.bodyMedium,
                color = Yellow100,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- CARD LOGIN ---
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
                        text = "Selamat Datang!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray700
                    )
                    Text(
                        text = "Silakan masuk ke akun Anda",
                        fontSize = 14.sp,
                        color = Gray500,
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    // Email Input
                    Text("Email", style = MaterialTheme.typography.labelMedium, color = Gray700)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = { viewModel.email = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("email@stis.ac.id", color = Gray400) },
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = Gray400) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Gray200,
                            focusedBorderColor = Blue700,
                            focusedContainerColor = Gray50
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(Modifier.height(16.dp))

                    // Password Input
                    Text("Password", style = MaterialTheme.typography.labelMedium, color = Gray700)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("••••••••", color = Gray400) },
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Gray400) },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(image, null, tint = Gray400)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Gray200,
                            focusedBorderColor = Blue700,
                            focusedContainerColor = Gray50
                        ),
                        singleLine = true
                    )

                    Spacer(Modifier.height(24.dp))

                    // Login Button (Gradient)
                    Button(
                        onClick = { viewModel.login { role -> onLoginSuccess(role) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(listOf(Blue900, Blue700, Yellow600)),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Masuk ke Akun", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Divider "ATAU"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(modifier = Modifier.weight(1f), color = Gray200)
                        Text("ATAU", modifier = Modifier.padding(horizontal = 12.dp), fontSize = 12.sp, color = Gray500)
                        Divider(modifier = Modifier.weight(1f), color = Gray200)
                    }

                    Spacer(Modifier.height(24.dp))

                    // Register Link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Belum punya akun? ", color = Gray500, fontSize = 14.sp)
                        Text(
                            text = "Registrasi",
                            color = Blue900,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Footer
            Text(
                text = "© 2026 Politeknik Statistika STIS",
                color = Blue100,
                fontSize = 12.sp
            )
        }
    }
}