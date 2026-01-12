package com.example.stisbanksoal.data.repository

import com.example.stisbanksoal.data.model.AuthResponse
import com.example.stisbanksoal.data.model.LoginRequest
import com.example.stisbanksoal.data.model.RegisterRequest
import com.example.stisbanksoal.data.remote.ApiService
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(request: LoginRequest): Response<AuthResponse> {
        return apiService.login(request)
    }

    suspend fun register(request: RegisterRequest): Response<Any> {
        return apiService.register(request)
    }
}