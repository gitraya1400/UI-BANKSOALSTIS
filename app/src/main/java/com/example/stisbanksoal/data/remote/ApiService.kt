package com.example.stisbanksoal.data.remote

import com.example.stisbanksoal.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTH ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Any>

    // --- PROFIL USER ---
    @GET("user/profile")
    suspend fun getProfile(@Header("Authorization") token: String): User

    // --- MATA KULIAH ---
    @GET("matakuliah")
    suspend fun getAllMataKuliah(@Header("Authorization") token: String): List<MataKuliah>

    @GET("matakuliah/dosen/{dosenId}")
    suspend fun getMataKuliahDosen(
        @Header("Authorization") token: String,
        @Path("dosenId") dosenId: Long
    ): List<MataKuliah>

    @POST("matakuliah")
    suspend fun createMataKuliah(@Header("Authorization") token: String, @Body mk: MataKuliah): MataKuliah

    @PUT("matakuliah/{id}")
    suspend fun updateMataKuliah(@Header("Authorization") token: String, @Path("id") id: Long, @Body mk: MataKuliah): MataKuliah

    @DELETE("matakuliah/{id}")
    suspend fun deleteMataKuliah(@Header("Authorization") token: String, @Path("id") id: Long): Response<Any>

    // --- ASSIGN DOSEN (Admin) ---
    @GET("user/dosen")
    suspend fun getAllDosen(@Header("Authorization") token: String): List<User>

    @POST("matakuliah/{mkId}/dosen/{dosenId}")
    suspend fun assignDosen(
        @Header("Authorization") token: String,
        @Path("mkId") mkId: Long,
        @Path("dosenId") dosenId: Long
    ): Response<Any>

    @DELETE("matakuliah/{mkId}/dosen/{dosenId}")
    suspend fun removeDosen(
        @Header("Authorization") token: String,
        @Path("mkId") mkId: Long,
        @Path("dosenId") dosenId: Long
    ): Response<Any>

    // --- PERTEMUAN ---
    @GET("pertemuan/matakuliah/{mkId}")
    suspend fun getPertemuanByMk(@Header("Authorization") token: String, @Path("mkId") mkId: Long): List<Pertemuan>

    @POST("pertemuan")
    suspend fun createPertemuan(@Header("Authorization") token: String, @Body p: Pertemuan): Pertemuan

    @DELETE("pertemuan/{id}")
    suspend fun deletePertemuan(@Header("Authorization") token: String, @Path("id") id: Long): Response<Any>

    // --- SOAL ---
    @GET("soal/search")
    suspend fun getSoalByPertemuan(
        @Header("Authorization") token: String,
        @Query("pertemuanId") pertemuanId: Long
    ): List<Soal>

    @POST("soal/pilihanganda")
    suspend fun createPilihanGanda(@Header("Authorization") token: String, @Body req: PilihanGandaRequest): Response<Any>

    @POST("soal/esai")
    suspend fun createEsai(@Header("Authorization") token: String, @Body req: EsaiRequest): Response<Any>

    @DELETE("soal/{id}")
    suspend fun deleteSoal(@Header("Authorization") token: String, @Path("id") id: Long): Response<Any>

    @Multipart
    @POST("soal/{id}/gambar")
    suspend fun uploadGambar(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Part file: MultipartBody.Part
    ): Response<Any>

    // --- UPDATE PROFIL & PASSWORD ---

    @PUT("user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<User>

    @PUT("user/password")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body request: UpdatePasswordRequest
    ): Response<Any>

} // <--- Pastikan kurung kurawal tutup ini ada, dan tidak ada kode lagi di bawahnya