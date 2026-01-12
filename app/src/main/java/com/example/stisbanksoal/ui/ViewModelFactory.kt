package com.example.stisbanksoal.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stisbanksoal.data.local.UserPreferences
import com.example.stisbanksoal.data.remote.ApiClient
import com.example.stisbanksoal.data.repository.AdminRepository
import com.example.stisbanksoal.data.repository.AuthRepository
import com.example.stisbanksoal.data.repository.DosenRepository // <-- INI IMPORT PENTING
import com.example.stisbanksoal.data.repository.SoalRepository
import com.example.stisbanksoal.ui.screens.admin.AdminDetailViewModel
import com.example.stisbanksoal.ui.screens.admin.AdminViewModel
import com.example.stisbanksoal.ui.screens.auth.AuthViewModel
import com.example.stisbanksoal.ui.screens.common.ProfileViewModel
import com.example.stisbanksoal.ui.screens.dosen.DosenViewModel // <-- INI IMPORT PENTING
import com.example.stisbanksoal.ui.screens.dosen.PertemuanViewModel
import com.example.stisbanksoal.ui.screens.dosen.SoalViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Objek pendukung yang dibutuhkan semua ViewModel
        val apiService = ApiClient.instance
        val userPreferences = UserPreferences(context)

        // 1. Jika yang diminta adalah AuthViewModel (Login/Register)
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val repo = AuthRepository(apiService)
            return AuthViewModel(repo, userPreferences) as T
        }

        // 2. Jika yang diminta adalah AdminViewModel (Dashboard Admin)
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            val repo = AdminRepository(apiService)
            return AdminViewModel(repo, userPreferences) as T
        }
        // 2. Admin Home
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            val repo = AdminRepository(apiService)
            return AdminViewModel(repo, userPreferences) as T
        }
        // 3. Jika yang diminta DosenViewModel
        if (modelClass.isAssignableFrom(DosenViewModel::class.java)) {
            val repo = DosenRepository(apiService)
            return DosenViewModel(repo, userPreferences) as T
        }

        // 4. Jika yang diminta PertemuanViewModel
        if (modelClass.isAssignableFrom(PertemuanViewModel::class.java)) {
            val repo = DosenRepository(apiService)
            return PertemuanViewModel(repo, userPreferences) as T
        }
        if (modelClass.isAssignableFrom(AdminDetailViewModel::class.java)) {
            val repo = AdminRepository(apiService)
            return AdminDetailViewModel(repo, userPreferences) as T
        }
        // 6. Soal Management
        if (modelClass.isAssignableFrom(SoalViewModel::class.java)) {
            val repo = SoalRepository(apiService)
            return SoalViewModel(repo, userPreferences) as T
        }
        // 7. [BARU] Profile ViewModel
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            // Kita bisa pakai AdminRepository karena dia punya fungsi getProfile
            // Atau repo lain yang punya fungsi getProfile
            val repo = AdminRepository(apiService)
            return ProfileViewModel(repo, userPreferences) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}