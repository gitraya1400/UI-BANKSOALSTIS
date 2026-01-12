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

    var mataKuliahList by mutableStateOf<List<MataKuliah>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // DATA USER DINAMIS
    var userName by mutableStateOf("Pengguna") // Default
    var userInitial by mutableStateOf("U")     // Default

    // Form State
    var inputKode by mutableStateOf("")
    var inputNama by mutableStateOf("")
    var inputSks by mutableStateOf("")
    var inputSemester by mutableStateOf("")
    var inputDeskripsi by mutableStateOf("")

    init {
        loadUserProfile() // Load data user saat init
        loadMataKuliah()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            // Ambil Nama dari UserPreferences (asumsi kamu menyimpan nama saat login)
            // Jika belum menyimpan nama, kita pakai "Admin" dulu atau ambil dari API Profile
            val name = userPreferences.userName.first() ?: "Administrator"
            userName = name

            // Ambil inisial (2 huruf pertama)
            userInitial = name.split(" ")
                .take(2).joinToString("") { it.firstOrNull()?.toString() ?: "" }
                .uppercase()
        }
    }

    fun loadMataKuliah() {
        viewModelScope.launch {
            isLoading = true
            try {
                val token = userPreferences.accessToken.first() ?: return@launch
                mataKuliahList = repository.getAllMataKuliah(token)
            } catch (e: Exception) {
                errorMessage = "Gagal load MK: ${e.message}"
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
                    sks = inputSks.toIntOrNull() ?: 2,
                    semester = inputSemester.toIntOrNull() ?: 1,
                    deskripsi = inputDeskripsi
                )
                repository.createMataKuliah(token, newMk)
                inputKode = ""
                inputNama = ""
                inputSks = ""
                inputSemester = ""
                inputDeskripsi = ""
                loadMataKuliah()
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Gagal buat MK: ${e.message}"
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
                loadMataKuliah()
            } catch (e: Exception) {
                errorMessage = "Gagal hapus: ${e.message}"
            }
        }
    }
}