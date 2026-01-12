package com.example.stisbanksoal.data.model

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val nip: String?,
    val role: String
)
data class UpdateProfileRequest(
    val name: String,
    val email: String
)

data class UpdatePasswordRequest(
    val passwordLama: String, // Sesuaikan dengan nama field di Backend (oldPassword?)
    val passwordBaru: String  // Sesuaikan dengan nama field di Backend (newPassword?)
)