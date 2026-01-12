package com.example.stisbanksoal.data.model

// Base response untuk List Soal
data class Soal(
    val id: Long,
    val pertanyaan: String,
    val tipeSoal: String,         // "PILIHAN_GANDA" atau "ESAI"
    val tingkatKesulitan: String, // "MUDAH", "SEDANG", "SULIT"
    val gambar: String?,          // Nama file gambar
    val pertemuanId: Long
)

// Untuk Create/Update Pilihan Ganda
data class PilihanGandaRequest(
    val pertanyaan: String,
    val pertemuanId: Long,
    val tingkatKesulitan: String,
    val pilihanJawaban: List<String>,
    val indexJawabanBenar: Int, // 0 = A, 1 = B, dst
    val pembahasan: String? = null
)

// Untuk Create/Update Esai
data class EsaiRequest(
    val pertanyaan: String,
    val pertemuanId: Long,
    val tingkatKesulitan: String,
    val jawabanKunci: String,
    val rubrik: String? = null
)