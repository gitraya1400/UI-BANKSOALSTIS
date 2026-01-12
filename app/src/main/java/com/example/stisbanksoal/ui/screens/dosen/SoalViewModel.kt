package com.example.stisbanksoal.ui.screens.dosen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stisbanksoal.data.local.UserPreferences
import com.example.stisbanksoal.data.model.EsaiRequest
import com.example.stisbanksoal.data.model.PilihanGandaRequest
import com.example.stisbanksoal.data.model.Soal
import com.example.stisbanksoal.data.repository.SoalRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class SoalViewModel(
    private val repository: SoalRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // DATA UTAMA
    var soalList by mutableStateOf<List<Soal>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // FILTER
    private var currentPertemuanId: Long = 0

    // FORM INPUT (Share field untuk PG & Esai)
    var tipeSoalInput by mutableStateOf("PILIHAN_GANDA") // atau "ESAI"
    var pertanyaanInput by mutableStateOf("")
    var tingkatKesulitanInput by mutableStateOf("MUDAH") // MUDAH, SEDANG, SULIT

    // KHUSUS PG
    var opsiA by mutableStateOf("")
    var opsiB by mutableStateOf("")
    var opsiC by mutableStateOf("")
    var opsiD by mutableStateOf("")
    var opsiE by mutableStateOf("")
    var kunciJawabanPG by mutableStateOf(0) // 0=A, 1=B, dst

    // KHUSUS ESAI
    var kunciJawabanEsai by mutableStateOf("")

    // LOAD DATA
    fun loadSoal(pertemuanId: Long) {
        currentPertemuanId = pertemuanId
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val token = userPreferences.accessToken.first() ?: return@launch
                soalList = repository.getSoalByPertemuan(token, pertemuanId)
            } catch (e: Exception) {
                errorMessage = "Gagal load soal: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // SIMPAN SOAL
    fun createSoal(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val token = userPreferences.accessToken.first() ?: return@launch

                if (tipeSoalInput == "PILIHAN_GANDA") {
                    val req = PilihanGandaRequest(
                        pertanyaan = pertanyaanInput,
                        pertemuanId = currentPertemuanId,
                        tingkatKesulitan = tingkatKesulitanInput,
                        pilihanJawaban = listOf(opsiA, opsiB, opsiC, opsiD, opsiE),
                        indexJawabanBenar = kunciJawabanPG,
                        pembahasan = "Pembahasan otomatis"
                    )
                    repository.createPilihanGanda(token, req)
                } else {
                    val req = EsaiRequest(
                        pertanyaan = pertanyaanInput,
                        pertemuanId = currentPertemuanId,
                        tingkatKesulitan = tingkatKesulitanInput,
                        jawabanKunci = kunciJawabanEsai
                    )
                    repository.createEsai(token, req)
                }

                // Reset Form
                resetForm()
                loadSoal(currentPertemuanId) // Refresh list
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Gagal simpan: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteSoal(id: Long) {
        viewModelScope.launch {
            try {
                val token = userPreferences.accessToken.first() ?: return@launch
                repository.deleteSoal(token, id)
                loadSoal(currentPertemuanId)
            } catch (e: Exception) {
                errorMessage = "Gagal hapus: ${e.message}"
            }
        }
    }

    // FUNGSI UPLOAD GAMBAR (Dipanggil terpisah setelah soal dibuat/saat edit)
    // Note: Untuk simplifikasi, di UI nanti kita buat tombol upload terpisah di list item
    fun uploadGambar(soalId: Long, file: File, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true
                val token = userPreferences.accessToken.first() ?: return@launch

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                repository.uploadGambar(token, soalId, body)
                loadSoal(currentPertemuanId)
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Upload gagal: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun resetForm() {
        pertanyaanInput = ""
        opsiA = ""; opsiB = ""; opsiC = ""; opsiD = ""; opsiE = ""
        kunciJawabanPG = 0
        kunciJawabanEsai = ""
    }
}