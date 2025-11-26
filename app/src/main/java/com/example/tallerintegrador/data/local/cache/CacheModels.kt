package com.example.tallerintegrador.data.local.cache

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tallerintegrador.data.model.pelicula

/**
 * ✅ Entidad de caché para películas
 * Almacena las películas localmente para evitar requests innecesarias
 */
@Entity(tableName = "peliculas_cache")
data class PeliculaCacheEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val posterUrl: String,
    val videoUrl: String,
    val durationMinutes: Int,
    val genre: String,
    val createdAt: String?,
    val updatedAt: String?,
    val cachedAt: Long = System.currentTimeMillis() // Timestamp del cache
)

/**
 * ✅ Entidad de caché para favoritos
 * Almacena los IDs de películas favoritas
 */
@Entity(tableName = "favoritos_cache")
data class FavoritoCacheEntity(
    @PrimaryKey
    val peliculaId: Int,
    val cachedAt: Long = System.currentTimeMillis()
)

// ========== FUNCIONES DE CONVERSIÓN ==========

/**
 * Convierte de modelo de red a entidad de cache
 */
fun pelicula.toCacheEntity(): PeliculaCacheEntity {
    return PeliculaCacheEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        posterUrl = this.posterUrl,
        videoUrl = this.videoUrl,
        durationMinutes = this.durationMinutes,
        genre = this.genre,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * Convierte de entidad de cache a modelo de dominio
 */
fun PeliculaCacheEntity.toPelicula(): pelicula {
    return pelicula(
        id = this.id,
        title = this.title,
        description = this.description,
        posterUrl = this.posterUrl,
        videoUrl = this.videoUrl,
        durationMinutes = this.durationMinutes,
        genre = this.genre,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}