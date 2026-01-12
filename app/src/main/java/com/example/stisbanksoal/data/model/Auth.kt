package com.example.stisbanksoal.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val nip: String? = null
)

data class AuthResponse(
    val email: String,
    val name: String,
    val role: String,       // "ADMIN" atau "DOSEN"
    val accessToken: String,
    val tokenType: String
)