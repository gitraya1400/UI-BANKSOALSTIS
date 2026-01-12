package com.example.stisbanksoal.data.model

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val nip: String?,
    val role: String
)