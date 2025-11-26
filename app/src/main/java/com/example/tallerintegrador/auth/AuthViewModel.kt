// app/src/main/java/com/example/tallerintegrador/auth/AuthViewModel.kt
package com.example.tallerintegrador.auth

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerintegrador.auth.state.AuthState
import com.example.tallerintegrador.data.local.TokenManager
import com.example.tallerintegrador.data.model.*
import com.example.tallerintegrador.data.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    val authState = mutableStateOf<AuthState>(AuthState.Idle)

    init {
        // Verificar si hay sesión activa al iniciar
        if (tokenManager.isLoggedIn()) {
            authState.value = AuthState.Success(
                LoginResponse(
                    accessToken = tokenManager.getToken() ?: "",
                    tokenType = "Bearer",
                    user = User(
                        id = tokenManager.getUserId(),
                        name = tokenManager.getUserName() ?: "",
                        email = tokenManager.getUserEmail() ?: ""
                    )
                )
            )
        }
    }

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            try {
                val response = apiService.login(loginRequest)

                tokenManager.saveAuthData(
                    token = response.accessToken,
                    userId = response.user.id,
                    userName = response.user.name,
                    userEmail = response.user.email
                )

                authState.value = AuthState.Success(response)
                Log.d("AuthViewModel", "Login exitoso - Token guardado")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login failed", e)
                authState.value = AuthState.Error("Error en el login: ${e.message}")
            }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            try {
                val response = apiService.register(registerRequest)

                tokenManager.saveAuthData(
                    token = response.accessToken,
                    userId = response.user.id,
                    userName = response.user.name,
                    userEmail = response.user.email
                )

                authState.value = AuthState.Success(response)
                Log.d("AuthViewModel", "Registro exitoso - Token guardado")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration failed", e)
                authState.value = AuthState.Error("Error en el registro: ${e.message}")
            }
        }
    }

    fun logout() {
        tokenManager.clearSession()
        authState.value = AuthState.Idle
        Log.d("AuthViewModel", "Sesión cerrada")
    }

    fun getToken(): String? = tokenManager.getToken()
}