package com.example.stisbanksoal.ui.screens.dosen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stisbanksoal.data.local.UserPreferences
import com.example.stisbanksoal.data.model.MataKuliah
import com.example.stisbanksoal.data.repository.DosenRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DosenViewModel(
    private val repository: DosenRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    var mataKuliahList by mutableStateOf<List<MataKuliah>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var dosenName by mutableStateOf("")

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val token = userPreferences.accessToken.first()
                if (token != null) {
                    // 1. Ambil data user dulu (butuh ID-nya)
                    val user = repository.getProfile(token)
                    dosenName = user.name

                    // 2. Ambil MK berdasarkan ID user tadi
                    mataKuliahList = repository.getMataKuliahDosen(token, user.id)
                }
            } catch (e: Exception) {
                errorMessage = "Gagal memuat data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}