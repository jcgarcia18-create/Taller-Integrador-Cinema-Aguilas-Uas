package com.example.tallerintegrador.data.local.cache

import com.example.tallerintegrador.data.model.pelicula
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheManager @Inject constructor(
    private val peliculaDao: PeliculaCacheDao,
    private val favoritoDao: FavoritoCacheDao
) {
    companion object {
        private const val CACHE_VALIDITY_MS = 60 * 60 * 1000L // 1 hora
    }

    // ========== PELÍCULAS ==========

    suspend fun isPeliculasCacheValid(): Boolean {
        val validTimestamp = System.currentTimeMillis() - CACHE_VALIDITY_MS
        val hasData = peliculaDao.getCacheSize() > 0
        val isValid = peliculaDao.isCacheValid(validTimestamp)
        return hasData && isValid
    }

    suspend fun getCachedPeliculas(): List<pelicula>? {
        return if (isPeliculasCacheValid()) {
            peliculaDao.getAllPeliculas().map { it.toPelicula() }
        } else {
            null
        }
    }

    suspend fun getCachedPelicula(id: Int): pelicula? {
        return peliculaDao.getPeliculaById(id)?.toPelicula()
    }

    suspend fun cachePeliculas(peliculas: List<pelicula>) {
        val entities = peliculas.map { it.toCacheEntity() }
        peliculaDao.insertPeliculas(entities)
    }

    suspend fun cachePelicula(pelicula: pelicula) {
        peliculaDao.insertPelicula(pelicula.toCacheEntity())
    }

    suspend fun clearPeliculasCache() {
        peliculaDao.clearCache()
    }

    // ========== FAVORITOS ==========

    suspend fun getCachedFavoritosIds(): List<Int> {
        return favoritoDao.getAllFavoritos().map { it.peliculaId }
    }

    fun getFavoritosIdsFlow(): Flow<Set<Int>> {
        return favoritoDao.getAllFavoritosFlow().map { list ->
            list.map { it.peliculaId }.toSet()
        }
    }

    suspend fun isFavorito(peliculaId: Int): Boolean {
        return favoritoDao.isFavorito(peliculaId)
    }

    fun isFavoritoFlow(peliculaId: Int): Flow<Boolean> {
        return favoritoDao.isFavoritoFlow(peliculaId)
    }

    suspend fun addFavoritoToCache(peliculaId: Int) {
        favoritoDao.insertFavorito(FavoritoCacheEntity(peliculaId))
    }

    suspend fun removeFavoritoFromCache(peliculaId: Int) {
        favoritoDao.deleteFavorito(peliculaId)
    }

    suspend fun syncFavoritos(peliculaIds: List<Int>) {
        favoritoDao.syncFavoritos(peliculaIds)
    }

    suspend fun clearFavoritosCache() {
        favoritoDao.clearCache()
    }

    suspend fun clearAllCache() {
        clearPeliculasCache()
        clearFavoritosCache()
    }
}