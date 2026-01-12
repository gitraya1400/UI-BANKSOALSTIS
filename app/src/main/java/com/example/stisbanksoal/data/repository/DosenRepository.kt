package com.example.stisbanksoal.data.repository

import com.example.stisbanksoal.data.model.MataKuliah
import com.example.stisbanksoal.data.model.Pertemuan
import com.example.stisbanksoal.data.model.User
import com.example.stisbanksoal.data.remote.ApiService

class DosenRepository(private val apiService: ApiService) {

    suspend fun getProfile(token: String): User {
        return apiService.getProfile("Bearer $token")
    }

    suspend fun getMataKuliahDosen(token: String, dosenId: Long): List<MataKuliah> {
        return apiService.getMataKuliahDosen("Bearer $token", dosenId)
    }

    // --- TAMBAHAN BARU ---
    suspend fun getPertemuanByMk(token: String, mkId: Long): List<Pertemuan> {
        return apiService.getPertemuanByMk("Bearer $token", mkId)
    }
}