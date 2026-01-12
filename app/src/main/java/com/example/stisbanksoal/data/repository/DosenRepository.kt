package com.example.stisbanksoal.data.repository

import com.example.stisbanksoal.data.model.EsaiRequest
import com.example.stisbanksoal.data.model.MataKuliah
import com.example.stisbanksoal.data.model.Pertemuan
import com.example.stisbanksoal.data.model.PilihanGandaRequest
import com.example.stisbanksoal.data.model.Soal
import com.example.stisbanksoal.data.model.User
import com.example.stisbanksoal.data.remote.ApiService
import okhttp3.MultipartBody
import retrofit2.Response

class DosenRepository(private val apiService: ApiService) {

    // --- DASHBOARD (Ambil MK yang diajar) ---
    // Menggunakan getMataKuliahDosen sesuai API
    suspend fun getMataKuliahDosen(token: String, dosenId: Long): List<MataKuliah> {
        return apiService.getMataKuliahDosen("Bearer $token", dosenId)
    }

    // --- PERTEMUAN ---
    suspend fun getPertemuanByMk(token: String, mkId: Long): List<Pertemuan> {
        return apiService.getPertemuanByMk("Bearer $token", mkId)
    }

    // --- SOAL ---
    suspend fun getSoalByPertemuan(token: String, pertemuanId: Long): List<Soal> {
        return apiService.getSoalByPertemuan("Bearer $token", pertemuanId)
    }

    suspend fun createPilihanGanda(token: String, req: PilihanGandaRequest): Response<Any> {
        return apiService.createPilihanGanda("Bearer $token", req)
    }

    suspend fun createEsai(token: String, req: EsaiRequest): Response<Any> {
        return apiService.createEsai("Bearer $token", req)
    }

    suspend fun deleteSoal(token: String, id: Long): Response<Any> {
        return apiService.deleteSoal("Bearer $token", id)
    }

    suspend fun uploadGambar(token: String, id: Long, file: MultipartBody.Part): Response<Any> {
        return apiService.uploadGambar("Bearer $token", id, file)
    }

    // --- PROFIL (Agar Dosen juga bisa load profil) ---
    suspend fun getProfile(token: String): User {
        return apiService.getProfile("Bearer $token")
    }
}