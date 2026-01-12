package com.example.stisbanksoal.data.model

data class Pertemuan(
    val id: Long = 0, // ID biasanya digenerate backend/database, tapi tetap butuh di Android
    val nomorPertemuan: Int,
    val judul: String,
    val deskripsi: String,
    val mataKuliahId: Long // Tetap butuh ini untuk relasi
)