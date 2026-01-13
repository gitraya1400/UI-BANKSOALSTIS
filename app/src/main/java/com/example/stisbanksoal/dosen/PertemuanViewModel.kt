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

    fun loadPertemuan(mataKuliahId: Long) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val token = userPreferences.accessToken.first() ?: return@launch
                pertemuanList = repository.getPertemuanByMk(token, mataKuliahId)
            } catch (e: Exception) {
                errorMessage = "Gagal memuat pertemuan: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}