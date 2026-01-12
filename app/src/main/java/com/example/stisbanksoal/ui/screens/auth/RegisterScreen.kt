package com.example.stisbanksoal.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stisbanksoal.ui.ViewModelFactory

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit, // Callback balik ke Login
    onBackToLogin: () -> Unit
) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: AuthViewModel = viewModel(factory = factory)

    // Pantau jika registrasi sukses
    LaunchedEffect(viewModel.registerSuccess) {
        if (viewModel.registerSuccess) {
            Toast.makeText(context, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_LONG).show()
            onRegisterSuccess()
        }
    }

    // Pantau error
    LaunchedEffect(viewModel.loginError) {
        viewModel.loginError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Daftar Akun Baru", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Nama
        OutlinedTextField(
            value = viewModel.regName,
            onValueChange = { viewModel.regName = it },
            label = { Text("Nama Lengkap") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Email
        OutlinedTextField(
            value = viewModel.regEmail,
            onValueChange = { viewModel.regEmail = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Password
        OutlinedTextField(
            value = viewModel.regPassword,
            onValueChange = { viewModel.regPassword = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // NIP (Opsional)
        OutlinedTextField(
            value = viewModel.regNip,
            onValueChange = { viewModel.regNip = it },
            label = { Text("NIP (Opsional)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Daftar
        Button(
            onClick = { viewModel.register { /* Callback ditangani LaunchedEffect */ } },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Daftar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Sudah punya akun? Login")
        }
    }
}