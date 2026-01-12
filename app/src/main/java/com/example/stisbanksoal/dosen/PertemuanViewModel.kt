package com.example.stisbanksoal.ui.screens.dosen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stisbanksoal.data.local.UserPreferences
import com.example.stisbanksoal.data.model.Pertemuan
import com.example.stisbanksoal.data.repository.DosenRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PertemuanViewModel(
    private val repository: DosenRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    var pertemuanList by mutableStateOf<List<Pertemuan>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Fungsi ini dipanggil saat layar dibuka
    fun loadPertemuan(mkId: Long) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val token = userPreferences.accessToken.first()
                if (token != null) {
                    pertemuanList = repository.getPertemuanByMk(token, mkId)
                }
            } catch (e: Exception) {
                errorMessage = "Gagal memuat pertemuan: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}