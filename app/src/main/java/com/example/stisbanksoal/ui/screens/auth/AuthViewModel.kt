package com.example.stisbanksoal.ui.screens.auth

import androidx.compose.runtime.getValue  // <--- INI PENTING (Dulu hilang)
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue  // <--- INI PENTING (Dulu hilang)
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stisbanksoal.data.local.UserPreferences
import com.example.stisbanksoal.data.model.LoginRequest
import com.example.stisbanksoal.data.model.RegisterRequest
import com.example.stisbanksoal.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // STATE (Status UI)
    // Karena sudah ada import getValue & setValue, kata kunci 'by' sekarang aman
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    // Register State
    var regName by mutableStateOf("")
    var regEmail by mutableStateOf("")
    var regPassword by mutableStateOf("")
    var regNip by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)
    var registerSuccess by mutableStateOf(false)

    fun login(onSuccess: (String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            loginError = "Email dan Password harus diisi"
            return
        }

        viewModelScope.launch {
            isLoading = true
            loginError = null
            try {
                val response = repository.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        // Simpan token ke HP
                        userPreferences.saveAuthToken(
                            token = body.accessToken,
                            role = body.role,
                            name = body.name,
                            email = body.email
                        )
                        onSuccess(body.role)
                    } else {
                        loginError = "Response kosong dari server"
                    }
                } else {
                    loginError = "Login gagal: Email atau password salah"
                }
            } catch (e: Exception) {
                loginError = "Terjadi kesalahan: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun register(onSuccess: () -> Unit) {
        if (regName.isBlank() || regEmail.isBlank() || regPassword.isBlank()) {
            loginError = "Semua field wajib diisi"
            return
        }

        viewModelScope.launch {
            isLoading = true
            loginError = null
            try {
                // Perbaikan logika NIP: Jika kosong, kirim null
                val nipFinal = if (regNip.isBlank()) null else regNip

                val request = RegisterRequest(regName, regEmail, regPassword, nipFinal)
                val response = repository.register(request)

                // Cek sukses (200 OK atau 201 Created)
                if (response.isSuccessful || response.code() == 201) {
                    registerSuccess = true
                    onSuccess()
                } else {
                    loginError = "Registrasi gagal. Email/NIP mungkin sudah dipakai."
                }
            } catch (e: Exception) {
                loginError = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}