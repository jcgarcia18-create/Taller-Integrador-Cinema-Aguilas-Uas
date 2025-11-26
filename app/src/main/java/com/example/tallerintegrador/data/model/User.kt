package com.example.tallerintegrador.data.model

import com.google.gson.annotations.SerializedName

/**
 * Representa la estructura de datos del usuario recibida desde la API.
 */
data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("suscripcion_activa")
    val suscripcionActiva: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String? = null
)

/**
 * Objeto enviado al servidor para iniciar sesión.
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Respuesta recibida del servidor tras un inicio de sesión exitoso.
 */
data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("token_type")
    val tokenType: String,

    val user: User
)

/**
 * Objeto enviado al servidor para registrar un nuevo usuario.
 */
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
