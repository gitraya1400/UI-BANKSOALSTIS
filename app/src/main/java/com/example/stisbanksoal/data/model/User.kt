package com.example.stisbanksoal.data.model

import com.google.gson.annotations.SerializedName // Pastikan baris ini ada!

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val nip: String?,
    val role: String,
    val accessToken: String? = null
)

// [PERBAIKAN] Sesuaikan dengan UpdateProfileDto.java
data class UpdateProfileRequest(
    @SerializedName("name") // Harus "name" sesuai backend
    val name: String,

    @SerializedName("email") // Harus "email" sesuai backend
    val email: String,

    @SerializedName("nip") // Harus "nip" sesuai backend
    val nip: String?
)

// [PERBAIKAN] Sesuaikan dengan ChangePasswordDto.java
data class UpdatePasswordRequest(
    @SerializedName("oldPassword") // Backend minta "oldPassword", bukan "passwordLama"
    val passwordLama: String,

    @SerializedName("newPassword") // Backend minta "newPassword", bukan "passwordBaru"
    val passwordBaru: String
)