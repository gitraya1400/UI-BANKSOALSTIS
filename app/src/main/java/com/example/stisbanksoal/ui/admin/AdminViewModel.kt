package com.example.stisbanksoal.ui.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stisbanksoal.data.local.UserPreferences
import com.example.stisbanksoal.data.model.MataKuliah
import com.example.stisbanksoal.data.repository.AdminRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: AdminRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // DATA UTAMA: List Mata Kuliah
    var mataKuliahList by mutableStateOf<List<MataKuliah>>(emptyList())

    // STATE STATUS
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // FORM INPUT (Untuk Tambah/Edit MK)
    var inputKode by mutableStateOf("")
    var inputNama by mutableStateOf("")
    var inputSks by mutableStateOf("")
    var inputSemester by mutableStateOf("")
    var inputDeskripsi by mutableStateOf("")

    // Inisialisasi: Langsung ambil data pas dibuka
    init {
        loadMataKuliah()
    }

    fun loadMataKuliah() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Ambil token dari memori HP
                val token = userPreferences.accessToken.first()
                if (token != null) {
                    mataKuliahList = repository.getAllMataKuliah(token)
                } else {
                    errorMessage = "Token tidak ditemukan, silakan login ulang"
                }
            } catch (e: Exception) {
                errorMessage = "Gagal load data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createMataKuliah(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val token = userPreferences.accessToken.first() ?: return@launch

                val newMk = MataKuliah(
                    kode = inputKode,
                    nama = inputNama,
                    sks = inputSks.toIntOrNull() ?: 0,
                    semester = inputSemester.toIntOrNull() ?: 1,
                    deskripsi = inputDeskripsi
                )

                repository.createMataKuliah(token, newMk)

                // Reset Form & Refresh Data
                resetForm()
                loadMataKuliah()
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Gagal membuat MK: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteMataKuliah(id: Long) {
        viewModelScope.launch {
            try {
                val token = userPreferences.accessToken.first() ?: return@launch
                repository.deleteMataKuliah(token, id)
                loadMataKuliah() // Refresh list setelah hapus
            } catch (e: Exception) {
                errorMessage = "Gagal hapus: ${e.message}"
            }
        }
    }

    private fun resetForm() {
        inputKode = ""
        inputNama = ""
        inputSks = ""
        inputSemester = ""
        inputDeskripsi = ""
    }
}