package com.example.stisbanksoal.ui.screens.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stisbanksoal.data.local.UserPreferences
import com.example.stisbanksoal.data.model.User
import com.example.stisbanksoal.data.repository.AdminRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: AdminRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Data User Asli
    var user by mutableStateOf<User?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // State untuk Edit Form
    var editName by mutableStateOf("")
    var editEmail by mutableStateOf("")

    // State untuk Password Form
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

                // PERBAIKAN: Panggil fungsi repository wrapper, bukan api service langsung
                user = repository.getProfile(token)

                user?.let {
                    editName = it.name
                    editEmail = it.email
                }
            } catch (e: Exception) {
                errorMessage = "Gagal memuat profil: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateProfile(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                // Simulasi API Update
                delay(1000)

                user = user?.copy(name = editName, email = editEmail)

                val token = userPreferences.accessToken.first() ?: ""

                // Panggil saveUser (Pastikan UserPreferences sudah diupdate)
                userPreferences.saveUser(
                    id = user?.id ?: 0,
                    name = editName,
                    role = user?.role ?: "dosen",
                    token = token
                )

                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Gagal update: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun changePassword(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                delay(1000)
                oldPassword = ""
                newPassword = ""
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Gagal ganti password: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}