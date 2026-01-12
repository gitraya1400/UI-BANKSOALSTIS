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

    var pertemuanList by mutableStateOf<List<Pertemuan>>(emptyList())
    var allDosenList by mutableStateOf<List<User>>(emptyList())

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var inputJudulPertemuan by mutableStateOf("")
    var inputTanggalPertemuan by mutableStateOf("")

    private var currentMkId: Long = 0

    fun loadData(mkId: Long) {
        currentMkId = mkId
        viewModelScope.launch {
            isLoading = true
            try {
                val token = userPreferences.accessToken.first() ?: return@launch
                pertemuanList = repository.getPertemuanByMk(token, mkId)
                allDosenList = repository.getAllDosen(token)
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
                val newPertemuan = Pertemuan(
                    judul = inputJudulPertemuan,
                    tanggal = inputTanggalPertemuan,
                    mataKuliahId = currentMkId
                )
                repository.createPertemuan(token, newPertemuan)
                inputJudulPertemuan = ""
                inputTanggalPertemuan = ""
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

    fun assignDosen(dosenId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val token = userPreferences.accessToken.first() ?: return@launch
                repository.assignDosen(token, currentMkId, dosenId)
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Gagal assign dosen: ${e.message}"
            }
        }
    }
}