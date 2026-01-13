package com.example.stisbanksoal.data.model

import com.google.gson.annotations.SerializedName

// RESPONSE: Data yang diterima dari server (untuk List & Detail)
data class Soal(
    val id: Long,
    val pertanyaan: String,
    val tipeSoal: String,         // "PILIHAN_GANDA" atau "ESAI"
    val tingkatKesulitan: String, // "MUDAH", "SEDANG", "SULIT"
    val gambar: String?,
    val pertemuanId: Long,

    // [PENTING] Tambahkan ini agar Detail tidak Force Close / Kosong
    // Sesuaikan nama field JSON dari backend (biasanya 'pilihanJawaban' atau 'daftarPilihan')
    @SerializedName("pilihanJawaban")
    val opsiJawaban: List<String> = emptyList(),

    @SerializedName("indexJawabanBenar")
    val kunciJawabanIndex: Int? = null,

    @SerializedName("jawabanKunci")
    val kunciJawabanEsai: String? = null
)

// REQUEST: Data yang dikirim ke server untuk Pilihan Ganda
data class PilihanGandaRequest(
    val pertanyaan: String,
    val pertemuanId: Long,
    val tingkatKesulitan: String,
    val pilihanJawaban: List<String>,
    val indexJawabanBenar: Int,
    val pembahasan: String? = "Belum ada pembahasan",

    // [BARU] Field Wajib Backend
    val tahunPembuatan: Int,
    val semester: String // Format: "Ganjil 2024/2025"
)

// REQUEST: Data yang dikirim ke server untuk Esai
data class EsaiRequest(
    val pertanyaan: String,
    val pertemuanId: Long,
    val tingkatKesulitan: String,
    val jawabanKunci: String,
    val rubrik: String? = "Belum ada rubrik",

    // [BARU] Field Wajib Backend (Sesuai EsaiDto)
    val poinPenilaian: Int = 100, // Default 100 jika tidak diinput
    val tahunPembuatan: Int,
    val semester: String // Format: "Ganjil 2024/2025"
)