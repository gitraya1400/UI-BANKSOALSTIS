package com.example.stisbanksoal.data.model

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val nip: String?, // Pastikan ada field NIP di sini
    val role: String,
    val accessToken: String? = null
)

// [UPDATE] Tambahkan nip di request
data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val nip: String? // Tambahan
)

data class UpdatePasswordRequest(
    val passwordLama: String,
    val passwordBaru: String
)