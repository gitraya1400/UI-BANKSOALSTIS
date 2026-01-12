package com.example.stisbanksoal.data.model

data class MataKuliah(
    val id: Long = 0,
    val kode: String,
    val nama: String,
    val semester: Int,
    val sks: Int,
    val deskripsi: String
)