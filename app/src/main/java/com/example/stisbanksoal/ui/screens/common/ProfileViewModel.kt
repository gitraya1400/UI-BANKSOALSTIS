package com.example.stisbanksoal.ui.screens.common

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stisbanksoal.data.local.UserPreferences
import com.example.stisbanksoal.data.model.User
import com.example.stisbanksoal.data.repository.AdminRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: AdminRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    var user by mutableStateOf<User?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null) // State untuk Error Toast

    // State Edit Form
    var editName by mutableStateOf("")
    var editEmail by mutableStateOf("")
    var editNip by mutableStateOf("") // [BARU]

    // State Password
    var oldPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            isLoading = true
            try {
                val token = userPreferences.accessToken.first() ?: return@launch
                user = repository.getProfile(token)
                user?.let {
                    editName = it.name
                    editEmail = it.email
                    editNip = it.nip ?: "" // Load NIP
                }
            } catch (e: Exception) {
                errorMessage = "Gagal load profil: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // [UPDATE] Menambahkan parameter onNavigateToLogin
    fun updateProfile(onSuccess: () -> Unit, onNavigateToLogin: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null // Reset error
            try {
                val token = userPreferences.accessToken.first() ?: return@launch

                // 1. Simpan email LAMA sebelum request ke server
                val oldEmail = user?.email

                // 2. Request ke server (backend akan update email jika valid)
                val updatedUser = repository.updateProfile(token, editName, editEmail, editNip)

                if (updatedUser != null) {
                    // 3. Cek apakah email BERUBAH?
                    // Jika oldEmail != newEmail, berarti token lama sudah tidak valid (identitas berubah)
                    if (oldEmail != null && !updatedUser.email.equals(oldEmail, ignoreCase = true)) {

                        // KASUS KHUSUS: Email Ganti -> Logout Paksa & Login Ulang
                        userPreferences.clearSession() // Hapus token lama dari HP
                        onNavigateToLogin()            // Panggil callback untuk pindah layar

                    } else {
                        // KASUS BIASA: Cuma ganti Nama/NIP -> Update UI seperti biasa
                        user = updatedUser

                        // Simpan data baru ke preference (token tetap sama)
                        userPreferences.saveUser(
                            id = updatedUser.id,
                            name = updatedUser.name,
                            email = updatedUser.email,
                            nip = updatedUser.nip ?: "",
                            role = updatedUser.role,
                            token = token
                        )
                        onSuccess()
                    }
                } else {
                    errorMessage = "Gagal update! Server menolak data."
                }
            } catch (e: Exception) {
                Log.e("ProfileVM", "Error update", e)
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun changePassword(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val token = userPreferences.accessToken.first() ?: return@launch
                val isSuccess = repository.updatePassword(token, oldPassword, newPassword)

                if (isSuccess) {
                    oldPassword = ""
                    newPassword = ""
                    onSuccess()
                } else {
                    errorMessage = "Password lama salah!"
                }
            } catch (e: Exception) {
                errorMessage = "Gagal ganti password: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Fungsi helper untuk reset error setelah ditampilkan di UI
    fun clearError() {
        errorMessage = null
    }
}