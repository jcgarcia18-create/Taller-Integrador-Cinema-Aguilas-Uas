package com.example.tallerintegrador.data.local.cache

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * ✅ DAO para el cache de películas
 */
@Dao
interface PeliculaCacheDao {

    @Query("SELECT * FROM peliculas_cache")
    suspend fun getAllPeliculas(): List<PeliculaCacheEntity>

    @Query("SELECT * FROM peliculas_cache WHERE id = :peliculaId")
    suspend fun getPeliculaById(peliculaId: Int): PeliculaCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeliculas(peliculas: List<PeliculaCacheEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPelicula(pelicula: PeliculaCacheEntity)

    @Query("DELETE FROM peliculas_cache")
    suspend fun clearCache()

    @Query("SELECT COUNT(*) FROM peliculas_cache")
    suspend fun getCacheSize(): Int

    /**
     * Verifica si el cache está expirado (más de 1 hora)
     */
    @Query("SELECT COUNT(*) > 0 FROM peliculas_cache WHERE cachedAt > :timestamp")
    suspend fun isCacheValid(timestamp: Long): Boolean
}

/**
 * ✅ DAO para el cache de favoritos
 */
@Dao
interface FavoritoCacheDao {

    @Query("SELECT * FROM favoritos_cache")
    suspend fun getAllFavoritos(): List<FavoritoCacheEntity>

    @Query("SELECT * FROM favoritos_cache")
    fun getAllFavoritosFlow(): Flow<List<FavoritoCacheEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favoritos_cache WHERE peliculaId = :peliculaId)")
    suspend fun isFavorito(peliculaId: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM favoritos_cache WHERE peliculaId = :peliculaId)")
    fun isFavoritoFlow(peliculaId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorito(favorito: FavoritoCacheEntity)

    @Query("DELETE FROM favoritos_cache WHERE peliculaId = :peliculaId")
    suspend fun deleteFavorito(peliculaId: Int)

    @Query("DELETE FROM favoritos_cache")
    suspend fun clearCache()

    @Transaction
    suspend fun syncFavoritos(peliculaIds: List<Int>) {
        clearCache()
        val entities = peliculaIds.map { FavoritoCacheEntity(it) }
        entities.forEach { insertFavorito(it) }
    }
}