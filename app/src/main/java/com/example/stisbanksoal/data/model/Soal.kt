package com.example.stisbanksoal.data.model

import com.google.gson.annotations.SerializedName

// Model Soal Lengkap
data class Soal(
    val id: Long,
    val pertanyaan: String,
    val tipeSoal: String,
    val tingkatKesulitan: String,
    val gambar: String?,
    val pertemuanId: Long,

    // Field Tambahan untuk UI Pilihan Ganda
    @SerializedName("pilihanJawaban")
    val opsiJawaban: List<String> = emptyList(),

    @SerializedName("indexJawabanBenar")
    val kunciJawabanIndex: Int? = null,

    @SerializedName("jawabanKunci")
    val kunciJawabanEsai: String? = null
)

// Request untuk Create PG
data class PilihanGandaRequest(
    val pertanyaan: String,
    val pertemuanId: Long,
    val tingkatKesulitan: String,
    val pilihanJawaban: List<String>,
    @SerializedName("indexJawabanBenar")
    val indexJawabanBenar: Int,
    val pembahasan: String? = null
)

// Request untuk Create Esai
data class EsaiRequest(
    val pertanyaan: String,
    val pertemuanId: Long,
    val tingkatKesulitan: String,
    val jawabanKunci: String,
    val rubrik: String? = null
)