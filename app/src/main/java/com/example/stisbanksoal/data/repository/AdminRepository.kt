package com.example.stisbanksoal.data.repository

import com.example.stisbanksoal.data.model.MataKuliah
import com.example.stisbanksoal.data.model.Pertemuan
import com.example.stisbanksoal.data.model.UpdatePasswordRequest
import com.example.stisbanksoal.data.model.UpdateProfileRequest
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

    suspend fun deleteMataKuliah(token: String, id: Long): Response<Any> {
        return apiService.deleteMataKuliah("Bearer $token", id)
    }

    // --- MANAJEMEN PERTEMUAN (Admin) ---
    suspend fun getPertemuanByMk(token: String, mkId: Long): List<Pertemuan> {
        return apiService.getPertemuanByMk("Bearer $token", mkId)
    }

    suspend fun createPertemuan(token: String, p: Pertemuan): Pertemuan {
        return apiService.createPertemuan("Bearer $token", p)
    }

    suspend fun deletePertemuan(token: String, id: Long): Response<Any> {
        return apiService.deletePertemuan("Bearer $token", id)
    }

    // --- MANAJEMEN DOSEN (Admin) ---
    suspend fun getAllDosen(token: String): List<User> {
        return apiService.getAllDosen("Bearer $token")
    }

    suspend fun assignDosen(token: String, mkId: Long, dosenId: Long): Response<Any> {
        return apiService.assignDosen("Bearer $token", mkId, dosenId)
    }

    suspend fun removeDosen(token: String, mkId: Long, dosenId: Long): Response<Any> {
        return apiService.removeDosen("Bearer $token", mkId, dosenId)
    }

    // --- PROFILE USER ---
    suspend fun getProfile(token: String): User {
        return apiService.getProfile("Bearer $token")
    }

    // [BARU] Update Profil
    suspend fun updateProfile(token: String, name: String, email: String): User? {
        val req = UpdateProfileRequest(name, email)
        val response = apiService.updateProfile("Bearer $token", req)
        return if (response.isSuccessful) response.body() else null
    }

    // [BARU] Ganti Password
    suspend fun updatePassword(token: String, oldPass: String, newPass: String): Boolean {
        val req = UpdatePasswordRequest(oldPass, newPass)
        val response = apiService.updatePassword("Bearer $token", req)
        return response.isSuccessful
    }
}
