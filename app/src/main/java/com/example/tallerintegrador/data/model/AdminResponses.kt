// app/src/main/java/com/example/tallerintegrador/data/model/AdminResponses.kt
package com.example.tallerintegrador.data.model

import com.google.gson.annotations.SerializedName

data class AdminDashboardResponse(
    @SerializedName("totalUsuarios")
    val totalUsuarios: Int,

    @SerializedName("totalPeliculas")
    val totalPeliculas: Int,

    @SerializedName("totalFavoritos")
    val totalFavoritos: Int,

    @SerializedName("peliculaMasPopular")
    val peliculaMasPopular: String?,

    @SerializedName("usuarioMasActivo")
    val usuarioMasActivo: String?
)

data class MessageResponse(
    @SerializedName("message")
    val message: String
)