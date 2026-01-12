package com.example.stisbanksoal.ui.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stisbanksoal.data.local.UserPreferences
import com.example.stisbanksoal.data.model.Pertemuan
import com.example.stisbanksoal.data.model.User
import com.example.stisbanksoal.data.repository.AdminRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AdminDetailViewModel(
    private val repository: AdminRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // DATA UTAMA
    var pertemuanList by mutableStateOf<List<Pertemuan>>(emptyList())
    var allDosenList by mutableStateOf<List<User>>(emptyList())
    var assignedDosenList by mutableStateOf<List<User>>(emptyList()) // List Dosen yang mengajar

    // STATE UI
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // FORM INPUT PERTEMUAN (UPDATED: Sesuai DTO Backend)
    var inputNomorPertemuan by mutableStateOf("") // String agar mudah diinput di TextField
    var inputJudulPertemuan by mutableStateOf("")
    var inputDeskripsi by mutableStateOf("")

    private var currentMkId: Long = 0

    fun loadData(mkId: Long) {
        currentMkId = mkId
        viewModelScope.launch {
            isLoading = true
            try {
                val token = userPreferences.accessToken.first() ?: return@launch

                // 1. Load Pertemuan
                pertemuanList = repository.getPertemuanByMk(token, mkId)

                // 2. Load Semua Dosen (Untuk pilihan assign)
                allDosenList = repository.getAllDosen(token)

                // 3. Load Dosen yang sudah Assign
                // Karena API saat ini belum support getAssignedDosen, list ini awalnya kosong
                // atau diisi manual saat assign berhasil.

            } catch (e: Exception) {
                errorMessage = "Gagal load: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createPertemuan(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val token = userPreferences.accessToken.first() ?: return@launch

                // Konversi Input
                val nomor = inputNomorPertemuan.toIntOrNull() ?: 1

                val newPertemuan = Pertemuan(
                    nomorPertemuan = nomor,
                    judul = inputJudulPertemuan,
                    deskripsi = inputDeskripsi,
                    mataKuliahId = currentMkId
                )

                repository.createPertemuan(token, newPertemuan)

                // Reset Form
                inputNomorPertemuan = ""
                inputJudulPertemuan = ""
                inputDeskripsi = ""

                loadData(currentMkId)
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Gagal buat pertemuan: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deletePertemuan(id: Long) {
        viewModelScope.launch {
            try {
                val token = userPreferences.accessToken.first() ?: return@launch
                repository.deletePertemuan(token, id)
                loadData(currentMkId)
            } catch (e: Exception) {
                errorMessage = "Gagal hapus: ${e.message}"
            }
        }
    }

    // --- FITUR DOSEN ---

    fun assignDosen(dosenId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val token = userPreferences.accessToken.first() ?: return@launch
                repository.assignDosen(token, currentMkId, dosenId)

                // --- FIX: Update Local List Manually ---
                // Find the dosen object from allDosenList
                val assignedDosen = allDosenList.find { it.id == dosenId }
                if (assignedDosen != null) {
                    // Add to assigned list if not already there
                    if (!assignedDosenList.contains(assignedDosen)) {
                        assignedDosenList = assignedDosenList + assignedDosen
                    }
                }

                loadData(currentMkId) // Refresh other data
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Gagal assign: ${e.message}"
            }
        }
    }

    // 2. Remove Dosen (Membatalkan Tugas)
    fun removeDosen(dosenId: Long) {
        viewModelScope.launch {
            try {
                val token = userPreferences.accessToken.first() ?: return@launch
                repository.removeDosen(token, currentMkId, dosenId)

                // --- FIX: Update Local List Manually ---
                assignedDosenList = assignedDosenList.filter { it.id != dosenId }

                loadData(currentMkId)
            } catch (e: Exception) {
                errorMessage = "Gagal hapus dosen: ${e.message}"
            }
        }
    }
}