package com.example.stisbanksoal.data.model

data class Pertemuan(
    val id: Long = 0,
    val judul: String,
    val tanggal: String, // Format YYYY-MM-DD
    val mataKuliahId: Long
)