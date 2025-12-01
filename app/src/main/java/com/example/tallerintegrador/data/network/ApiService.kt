package com.example.tallerintegrador.data.network

import com.example.tallerintegrador.data.model.*
import com.example.tallerintegrador.feature.admin.LogActividad
import com.example.tallerintegrador.feature.admin.Usuario
import retrofit2.http.*

interface ApiService {

    // ---------- AUTENTICACIÓN ----------

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): LoginResponse

    // ---------- PELÍCULAS ----------

    @GET("api/peliculas")
    suspend fun getPeliculas(): List<pelicula>

    // ✅ NUEVO: Obtener una película específica por ID
    @GET("api/peliculas/{id}")
    suspend fun getPeliculaById(@Path("id") peliculaId: Int): pelicula

    // ---------- FAVORITOS ----------

    @GET("api/favoritos")
    suspend fun getFavoritos(
        @Header("Authorization") authHeader: String
    ): List<pelicula> // <--- espera una lista directa

    @POST("api/favoritos/{peliculaId}")
    suspend fun addFavorito(
        @Header("Authorization") authHeader: String,
        @Path("peliculaId") peliculaId: Int
    ): AddFavoritoResponse

    @DELETE("api/favoritos/{peliculaId}")
    suspend fun removeFavorito(
        @Header("Authorization") authHeader: String,
        @Path("peliculaId") peliculaId: Int
    ): RemoveFavoritoResponse

    @GET("api/favoritos/check/{peliculaId}")
    suspend fun checkFavorito(
        @Header("Authorization") authHeader: String,
        @Path("peliculaId") peliculaId: Int
    ): CheckFavoritoResponse

    // ========== ADMIN ENDPOINTS ==========

    @GET("api/admin/dashboard")
    suspend fun getAdminDashboard(
        @Header("Authorization") authHeader: String
    ): AdminDashboardResponse

    @GET("api/admin/users")
    suspend fun getAdminUsers(
        @Header("Authorization") authHeader: String
    ): List<Usuario>

    @DELETE("api/admin/users/{id}")
    suspend fun deleteAdminUser(
        @Header("Authorization") authHeader: String,
        @Path("id") userId: Int
    ): MessageResponse

    @PUT("api/admin/users/{id}")
    suspend fun updateAdminUser(
        @Header("Authorization") authHeader: String,
        @Path("id") userId: Int,
        @Body request: Map<String, String>
    ): MessageResponse

    @GET("api/admin/peliculas")
    suspend fun getAdminPeliculas(
        @Header("Authorization") authHeader: String
    ): List<pelicula>

    @DELETE("api/admin/peliculas/{id}")
    suspend fun deleteAdminPelicula(
        @Header("Authorization") authHeader: String,
        @Path("id") peliculaId: Int
    ): MessageResponse

    @POST("api/admin/peliculas")
    suspend fun createAdminPelicula(
        @Header("Authorization") authHeader: String,
        @Body request: Map<String, String>
    ): pelicula

    @PUT("api/admin/peliculas/{id}")
    suspend fun updateAdminPelicula(
        @Header("Authorization") authHeader: String,
        @Path("id") peliculaId: Int,
        @Body request: Map<String, String>
    ): MessageResponse

    @GET("api/admin/logs")
    suspend fun getAdminLogs(
        @Header("Authorization") authHeader: String
    ): List<LogActividad>
}