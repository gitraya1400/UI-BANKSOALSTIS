package com.example.stisbanksoal.data.repository

import com.example.stisbanksoal.data.model.EsaiRequest
import com.example.stisbanksoal.data.model.PilihanGandaRequest
import com.example.stisbanksoal.data.model.Soal
import com.example.stisbanksoal.data.remote.ApiService
import okhttp3.MultipartBody
import retrofit2.Response

class SoalRepository(private val apiService: ApiService) {

    suspend fun getSoalByPertemuan(token: String, pertemuanId: Long): List<Soal> {
        return apiService.getSoalByPertemuan("Bearer $token", pertemuanId)
    }

    // --- CREATE ---
    suspend fun createPilihanGanda(token: String, request: PilihanGandaRequest): Response<Any> {
        return apiService.createPilihanGanda("Bearer $token", request)
    }

    suspend fun createEsai(token: String, request: EsaiRequest): Response<Any> {
        return apiService.createEsai("Bearer $token", request)
    }

    // --- UPDATE (Wajib Ada untuk Fitur Edit) ---
    suspend fun updatePilihanGanda(token: String, id: Long, request: PilihanGandaRequest): Response<Any> {
        return apiService.updatePilihanGanda("Bearer $token", id, request)
    }

    suspend fun updateEsai(token: String, id: Long, request: EsaiRequest): Response<Any> {
        return apiService.updateEsai("Bearer $token", id, request)
    }

    // --- DELETE & UPLOAD ---
    suspend fun deleteSoal(token: String, id: Long): Response<Any> {
        return apiService.deleteSoal("Bearer $token", id)
    }

    suspend fun uploadGambar(token: String, id: Long, imagePart: MultipartBody.Part): Response<Any> {
        return apiService.uploadGambar("Bearer $token", id, imagePart)
    }
}