package com.example.stisbanksoal.ui.screens.dosen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import java.util.Calendar

class SoalViewModel(
    private val repository: SoalRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // --- STATE UTAMA ---
    var soalList by mutableStateOf<List<Soal>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    private var currentPertemuanId: Long = 0

    // --- FORM INPUT ---
    var tipeSoalInput by mutableStateOf("PILIHAN_GANDA") // "PILIHAN_GANDA" atau "ESAI"
    var pertanyaanInput by mutableStateOf("")
    var tingkatKesulitanInput by mutableStateOf("MUDAH")

    // --- OPSI DINAMIS (Menggantikan opsiA, opsiB...) ---
    // Menggunakan list agar jumlah opsi bebas (bukan fix 5)
    var opsiJawabanList = mutableStateListOf<String>("", "")
    var kunciJawabanPG by mutableStateOf(0)

    // --- ESAI ---
    var kunciJawabanEsai by mutableStateOf("")

    // --- EDIT MODE (Wajib untuk memperbaiki error 'isEditMode') ---
    var isEditMode by mutableStateOf(false)
    var currentSoalId by mutableStateOf<Long?>(null)

    // --- MANAJEMEN OPSI ---
    fun addOpsi() {
        opsiJawabanList.add("")
    }

    fun removeOpsi(index: Int) {
        if (opsiJawabanList.size > 2) {
            opsiJawabanList.removeAt(index)
            // Reset kunci jika index bergeser
            if (kunciJawabanPG >= opsiJawabanList.size) kunciJawabanPG = 0
        }
    }

    fun updateOpsi(index: Int, value: String) {
        if (index in opsiJawabanList.indices) {
            opsiJawabanList[index] = value
        }
    }

    // --- FUNGSI EDIT (Memperbaiki error 'prepareEdit') ---
    fun prepareEdit(soal: Soal) {
        isEditMode = true
        currentSoalId = soal.id
        tipeSoalInput = soal.tipeSoal
        pertanyaanInput = soal.pertanyaan
        tingkatKesulitanInput = soal.tingkatKesulitan

        if (soal.tipeSoal == "PILIHAN_GANDA") {
            opsiJawabanList.clear()
            // Jika data dari server ada, pakai itu. Jika null, kasih default 2 kosong.
            val opsis = soal.opsiJawaban.ifEmpty { listOf("", "") }
            opsiJawabanList.addAll(opsis)
            kunciJawabanPG = soal.kunciJawabanIndex ?: 0
        } else {
            kunciJawabanEsai = soal.kunciJawabanEsai ?: ""
        }
    }

    // --- RESET FORM (Harus PUBLIC agar bisa diakses UI) ---
    fun resetForm() {
        isEditMode = false
        currentSoalId = null
        pertanyaanInput = ""
        tipeSoalInput = "PILIHAN_GANDA"
        opsiJawabanList.clear()
        opsiJawabanList.addAll(listOf("", ""))
        kunciJawabanPG = 0
        kunciJawabanEsai = ""
        errorMessage = null
    }

    // --- LOAD DATA ---
    fun loadSoal(pertemuanId: Long) {
        currentPertemuanId = pertemuanId
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val token = userPreferences.accessToken.first() ?: return@launch
                soalList = repository.getSoalByPertemuan(token, pertemuanId)
            } catch (e: Exception) {
                errorMessage = "Gagal memuat: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Helper Semester Format Backend
    private fun getCurrentSemesterString(): String {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        return if (month in Calendar.AUGUST..Calendar.JANUARY)
            "Ganjil $year/${year + 1}"
        else
            "Genap ${year - 1}/$year"
    }

    // --- SAVE SOAL (Create / Update) ---
    // Memperbaiki error 'saveSoal'
    fun saveSoal(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val token = userPreferences.accessToken.first() ?: return@launch
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val semesterString = getCurrentSemesterString()

                if (tipeSoalInput == "PILIHAN_GANDA") {
                    if (opsiJawabanList.any { it.isBlank() }) {
                        errorMessage = "Semua opsi jawaban harus diisi!"
                        return@launch
                    }

                    val req = PilihanGandaRequest(
                        pertanyaan = pertanyaanInput,
                        pertemuanId = currentPertemuanId,
                        tingkatKesulitan = tingkatKesulitanInput,
                        pilihanJawaban = opsiJawabanList.toList(),
                        indexJawabanBenar = kunciJawabanPG,
                        tahunPembuatan = currentYear,
                        semester = semesterString
                    )

                    if (isEditMode && currentSoalId != null) {
                        repository.updatePilihanGanda(token, currentSoalId!!, req)
                    } else {
                        repository.createPilihanGanda(token, req)
                    }

                } else {
                    val req = EsaiRequest(
                        pertanyaan = pertanyaanInput,
                        pertemuanId = currentPertemuanId,
                        tingkatKesulitan = tingkatKesulitanInput,
                        jawabanKunci = kunciJawabanEsai,
                        poinPenilaian = 100, // Wajib ada
                        tahunPembuatan = currentYear,
                        semester = semesterString
                    )

                    if (isEditMode && currentSoalId != null) {
                        repository.updateEsai(token, currentSoalId!!, req)
                    } else {
                        repository.createEsai(token, req)
                    }
                }

                resetForm()
                loadSoal(currentPertemuanId)
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Gagal simpan: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // --- DELETE ---
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

    // --- UPLOAD GAMBAR ---
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
}