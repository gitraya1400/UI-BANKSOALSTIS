package com.example.stisbanksoal.data.repository

import com.example.stisbanksoal.data.model.MataKuliah
import com.example.stisbanksoal.data.model.Pertemuan
import com.example.stisbanksoal.data.model.User
import com.example.stisbanksoal.data.remote.ApiService
import retrofit2.Response

class AdminRepository(private val apiService: ApiService) {

    // --- MATA KULIAH ---
    suspend fun getAllMataKuliah(token: String): List<MataKuliah> {
        return apiService.getAllMataKuliah("Bearer $token")
    }

    suspend fun createMataKuliah(token: String, mk: MataKuliah): MataKuliah {
        return apiService.createMataKuliah("Bearer $token", mk)
    }

    suspend fun deleteMataKuliah(token: String, id: Long) {
        apiService.deleteMataKuliah("Bearer $token", id)
    }

    // --- MANAJEMEN PERTEMUAN (Admin) ---
    suspend fun getPertemuanByMk(token: String, mkId: Long): List<Pertemuan> {
        return apiService.getPertemuanByMk("Bearer $token", mkId)
    }

    suspend fun createPertemuan(token: String, p: Pertemuan): Pertemuan {
        return apiService.createPertemuan("Bearer $token", p)
    }

    suspend fun deletePertemuan(token: String, id: Long) {
        apiService.deletePertemuan("Bearer $token", id)
    }

    // --- MANAJEMEN DOSEN (Admin) ---
    suspend fun getAllDosen(token: String): List<User> {
        return apiService.getAllDosen("Bearer $token")
    }

    // Ambil MK berdasarkan ID untuk melihat siapa pengajarnya (Update ApiService jika perlu endpoint spesifik,
    // tapi biasanya kita bisa lihat dari data MK atau endpoint khusus.
    // Asumsi: Kita pakai endpoint getMataKuliahById kalau backend menyediakan list pengajar di dalamnya)
    // Sesuai Controller kamu: getDosenMataKuliah ada di Controller, tapi return List<MataKuliahDto>.
    // Kita pakai assignDosen dan removeDosen saja yang jelas ada.

    suspend fun assignDosen(token: String, mkId: Long, dosenId: Long): Response<Any> {
        return apiService.assignDosen("Bearer $token", mkId, dosenId)
    }

    suspend fun removeDosen(token: String, mkId: Long, dosenId: Long): Response<Any> {
        return apiService.removeDosen("Bearer $token", mkId, dosenId)
    }
}